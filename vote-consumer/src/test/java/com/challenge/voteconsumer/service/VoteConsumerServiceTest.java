package com.challenge.voteconsumer.service;

import com.challenge.voteconsumer.client.UserClient;
import com.challenge.voteconsumer.client.dto.UserStatusDTO;
import com.challenge.voteconsumer.model.Vote;
import com.challenge.voteconsumer.model.VoteChoice;
import com.challenge.voteconsumer.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteConsumerServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private VoteConsumerService voteConsumerService;

    @Test
    void consumeVote_ShouldSaveVote_WhenUserIsAbleToVote() {
        Vote vote = Vote.builder()
                .agendaId("1")
                .associateId("123")
                .choice(VoteChoice.YES)
                .build();

        UserStatusDTO userStatus = new UserStatusDTO("ABLE_TO_VOTE");
        when(userClient.checkVoteStatus("123")).thenReturn(userStatus);

        voteConsumerService.consumeVote(vote);

        verify(userClient).checkVoteStatus("123");
        verify(voteRepository).save(vote);
    }

    @Test
    void consumeVote_ShouldDiscardVote_WhenUserIsUnableToVote() {
        Vote vote = Vote.builder()
                .agendaId("1")
                .associateId("123")
                .choice(VoteChoice.YES)
                .build();

        UserStatusDTO userStatus = new UserStatusDTO("UNABLE_TO_VOTE");
        when(userClient.checkVoteStatus("123")).thenReturn(userStatus);

        voteConsumerService.consumeVote(vote);

        verify(userClient).checkVoteStatus("123");
        verify(voteRepository, never()).save(any());
    }

    @Test
    void consumeVote_ShouldHandleDuplicateKeyException_Gracefully() {
        Vote vote = Vote.builder()
                .agendaId("1")
                .associateId("123")
                .choice(VoteChoice.YES)
                .build();

        UserStatusDTO userStatus = new UserStatusDTO("ABLE_TO_VOTE");
        when(userClient.checkVoteStatus("123")).thenReturn(userStatus);
        when(voteRepository.save(vote)).thenThrow(new DuplicateKeyException("Duplicate"));

        voteConsumerService.consumeVote(vote);

        verify(voteRepository).save(vote);
        // Should not throw exception
    }

    @Test
    void consumeVote_ShouldThrowException_WhenUserApiFails() {
        Vote vote = Vote.builder()
                .agendaId("1")
                .associateId("123")
                .choice(VoteChoice.YES)
                .build();

        when(userClient.checkVoteStatus("123")).thenThrow(new RuntimeException("API Down"));

        assertThrows(RuntimeException.class, () -> voteConsumerService.consumeVote(vote));

        verify(voteRepository, never()).save(any());
    }
}
