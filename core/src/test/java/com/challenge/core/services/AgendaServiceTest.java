package com.challenge.core.services;

import com.challenge.core.exception.BadRequestException;
import com.challenge.core.exception.NotFoundException;
import com.challenge.core.mappers.AgendaMapper;
import com.challenge.core.model.Agenda;
import com.challenge.core.model.AgendaStatus;
import com.challenge.core.model.dto.AgendaResponseDTO;
import com.challenge.core.model.dto.CreateAgendaRequestDTO;
import com.challenge.core.repositories.AgendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private AgendaMapper agendaMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AgendaService agendaService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void create_ShouldReturnAgendaResponseDTO() {
        CreateAgendaRequestDTO request = new CreateAgendaRequestDTO("Test Agenda");
        Agenda agenda = new Agenda("Test Agenda");
        Agenda savedAgenda = new Agenda("Test Agenda");
        savedAgenda.setId("1");
        AgendaResponseDTO responseDTO = AgendaResponseDTO.builder().id("1").title("Test Agenda").build();

        when(agendaMapper.toEntity(request)).thenReturn(agenda);
        when(agendaRepository.save(agenda)).thenReturn(savedAgenda);
        when(agendaMapper.toResponseDTO(savedAgenda)).thenReturn(responseDTO);

        AgendaResponseDTO result = agendaService.create(request);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Test Agenda", result.getTitle());
        verify(agendaRepository).save(agenda);
    }

    @Test
    void findById_ShouldReturnAgenda_WhenExists() {
        String id = "1";
        Agenda agenda = new Agenda("Test Agenda");
        agenda.setId(id);
        AgendaResponseDTO responseDTO = AgendaResponseDTO.builder().id(id).title("Test Agenda").build();

        when(agendaRepository.findById(id)).thenReturn(Optional.of(agenda));
        when(agendaMapper.toResponseDTO(agenda)).thenReturn(responseDTO);

        AgendaResponseDTO result = agendaService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenNotExists() {
        String id = "1";
        when(agendaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> agendaService.findById(id));
    }

    @Test
    void openSession_ShouldOpenSession_WhenStatusIsCreated() {
        String id = "1";
        Agenda agenda = new Agenda("Test Agenda");
        agenda.setId(id);
        agenda.setStatus(AgendaStatus.CREATED);
        
        Agenda savedAgenda = new Agenda("Test Agenda");
        savedAgenda.setId(id);
        savedAgenda.setStatus(AgendaStatus.OPEN);
        
        AgendaResponseDTO responseDTO = AgendaResponseDTO.builder().id(id).status(AgendaStatus.OPEN).build();

        when(agendaRepository.findById(id)).thenReturn(Optional.of(agenda));
        when(agendaRepository.save(any(Agenda.class))).thenReturn(savedAgenda);
        when(agendaMapper.toResponseDTO(savedAgenda)).thenReturn(responseDTO);

        AgendaResponseDTO result = agendaService.openSession(id, 1L);

        assertNotNull(result);
        assertEquals(AgendaStatus.OPEN, result.getStatus());
        verify(valueOperations).set(eq("agenda:1:status"), eq("OPEN"), eq(1L), eq(TimeUnit.MINUTES));
    }

    @Test
    void openSession_ShouldThrowBadRequest_WhenStatusIsOpen() {
        String id = "1";
        Agenda agenda = new Agenda("Test Agenda");
        agenda.setId(id);
        agenda.setStatus(AgendaStatus.OPEN);

        when(agendaRepository.findById(id)).thenReturn(Optional.of(agenda));

        assertThrows(BadRequestException.class, () -> agendaService.openSession(id, 1L));
        verify(agendaRepository, never()).save(any());
    }

    @Test
    void openSession_ShouldThrowBadRequest_WhenStatusIsClosed() {
        String id = "1";
        Agenda agenda = new Agenda("Test Agenda");
        agenda.setId(id);
        agenda.setStatus(AgendaStatus.CLOSED);

        when(agendaRepository.findById(id)).thenReturn(Optional.of(agenda));

        assertThrows(BadRequestException.class, () -> agendaService.openSession(id, 1L));
        verify(agendaRepository, never()).save(any());
    }
}
