package com.challenge.core.services;

import com.challenge.core.exception.BadRequestException;
import com.challenge.core.model.VoteChoice;
import com.challenge.core.model.dto.VoteRequestDTO;
import com.challenge.core.model.dto.VoteResponseDTO;
import com.challenge.core.model.events.RegisterVoteEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteProducer voteProducer;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private VoteService voteService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void registerVote_ShouldSendVote_WhenSessionIsOpen() {
        String agendaId = "1";
        VoteRequestDTO request = new VoteRequestDTO("user1", VoteChoice.YES);
        
        when(valueOperations.get("agenda:1:status")).thenReturn("OPEN");

        VoteResponseDTO result = voteService.registerVote(agendaId, request);

        assertNotNull(result);
        assertEquals(agendaId, result.getAgendaId());
        assertEquals("user1", result.getAssociateId());
        assertEquals(VoteChoice.YES, result.getChoice());
        assertNotNull(result.getVotedAt());
        
        verify(voteProducer).sendVote(any(RegisterVoteEvent.class));
    }

    @Test
    void registerVote_ShouldThrowBadRequest_WhenSessionIsClosed() {
        String agendaId = "1";
        VoteRequestDTO request = new VoteRequestDTO("user1", VoteChoice.YES);
        
        when(valueOperations.get("agenda:1:status")).thenReturn(null);

        assertThrows(BadRequestException.class, () -> voteService.registerVote(agendaId, request));
        verify(voteProducer, never()).sendVote(any());
    }
}
