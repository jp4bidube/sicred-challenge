package com.challenge.voteconsumer.service;

import com.challenge.voteconsumer.model.VoteChoice;
import com.challenge.voteconsumer.model.events.AgendaClosedEvent;
import com.challenge.voteconsumer.model.events.VotingResultCalculatedEvent;
import com.challenge.voteconsumer.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaClosedListenerTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AgendaClosedListener agendaClosedListener;

    @Test
    void handleAgendaClosed_ShouldCalculateAndSendResults() {
        String agendaId = "1";
        AgendaClosedEvent event = new AgendaClosedEvent(agendaId);

        when(voteRepository.countByAgendaIdAndChoice(agendaId, VoteChoice.YES)).thenReturn(10L);
        when(voteRepository.countByAgendaIdAndChoice(agendaId, VoteChoice.NO)).thenReturn(5L);

        agendaClosedListener.handleAgendaClosed(event);

        verify(voteRepository).countByAgendaIdAndChoice(agendaId, VoteChoice.YES);
        verify(voteRepository).countByAgendaIdAndChoice(agendaId, VoteChoice.NO);
        
        VotingResultCalculatedEvent expectedResult = VotingResultCalculatedEvent.builder()
                .agendaId(agendaId)
                .votesYes(10L)
                .votesNo(5L)
                .build();
        
        verify(kafkaTemplate).send(eq("voting-result"), any(VotingResultCalculatedEvent.class));
    }
}
