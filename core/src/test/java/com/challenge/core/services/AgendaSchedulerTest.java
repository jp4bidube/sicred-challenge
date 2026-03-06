package com.challenge.core.services;

import com.challenge.core.model.Agenda;
import com.challenge.core.model.AgendaStatus;
import com.challenge.core.repositories.AgendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaSchedulerTest {

    @Mock
    private AgendaService agendaService;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private AgendaEventProducer agendaEventProducer;

    @Mock
    private ValueOperations<String, String> valueOperations; // Mock ValueOperations

    @InjectMocks
    private AgendaScheduler agendaScheduler;

    @Test
    void closeExpiredSessions_ShouldCloseExpiredAgendas() {
        Agenda expiredAgenda1 = Agenda.builder()
                .id("1")
                .title("Agenda 1")
                .status(AgendaStatus.OPEN)
                .sessionEndsAt(OffsetDateTime.now().minusMinutes(5))
                .build();

        Agenda expiredAgenda2 = Agenda.builder()
                .id("2")
                .title("Agenda 2")
                .status(AgendaStatus.OPEN)
                .sessionEndsAt(OffsetDateTime.now().minusMinutes(10))
                .build();

        when(agendaService.findOpenSessionsExpired()).thenReturn(Arrays.asList(expiredAgenda1, expiredAgenda2));

        agendaScheduler.closeExpiredSessions();

        verify(agendaRepository, times(1)).save(expiredAgenda1);
        verify(agendaRepository, times(1)).save(expiredAgenda2);
        verify(agendaEventProducer, times(1)).sendAgendaClosedEvent("1");
        verify(agendaEventProducer, times(1)).sendAgendaClosedEvent("2");
        verify(redisTemplate, times(1)).delete("agenda:1:status");
        verify(redisTemplate, times(1)).delete("agenda:2:status");
        
        assertEquals(AgendaStatus.CLOSED, expiredAgenda1.getStatus());
        assertEquals(AgendaStatus.CLOSED, expiredAgenda2.getStatus());
    }

    @Test
    void closeExpiredSessions_ShouldDoNothing_WhenNoExpiredAgendas() {
        when(agendaService.findOpenSessionsExpired()).thenReturn(Collections.emptyList());

        agendaScheduler.closeExpiredSessions();

        verify(agendaRepository, never()).save(any(Agenda.class));
        verify(agendaEventProducer, never()).sendAgendaClosedEvent(anyString());
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void closeExpiredSessions_ShouldHandleException_WhenSavingFails() {
        Agenda expiredAgenda = Agenda.builder()
                .id("1")
                .title("Agenda 1")
                .status(AgendaStatus.OPEN)
                .sessionEndsAt(OffsetDateTime.now().minusMinutes(5))
                .build();

        when(agendaService.findOpenSessionsExpired()).thenReturn(Collections.singletonList(expiredAgenda));
        doThrow(new RuntimeException("DB error")).when(agendaRepository).save(any(Agenda.class));
        // Removed unnecessary stubbing for redisTemplate

        agendaScheduler.closeExpiredSessions();

        verify(agendaRepository, times(1)).save(expiredAgenda);
        verify(agendaEventProducer, never()).sendAgendaClosedEvent(anyString()); // Event not sent if save fails
        verify(redisTemplate, never()).delete(anyString()); // Redis not deleted if save fails
    }
}
