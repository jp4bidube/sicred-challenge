package com.challenge.voteconsumer.service;

import com.challenge.voteconsumer.client.UserClient;
import com.challenge.voteconsumer.client.dto.UserStatusDTO;
import com.challenge.voteconsumer.model.Vote;
import com.challenge.voteconsumer.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VoteConsumerService {

    private static final Logger log = LoggerFactory.getLogger(VoteConsumerService.class);
    private final VoteRepository voteRepository;
    private final UserClient userClient;

    @KafkaListener(topics = "votes", groupId = "vote-consumer-group")
    public void consumeVote(Vote vote) {
        log.info("Received vote: {}", vote);
        
        try {
            try {
                UserStatusDTO userStatus = userClient.checkVoteStatus(vote.getAssociateId());
                if ("UNABLE_TO_VOTE".equals(userStatus.getStatus())) {
                    log.warn("User {} is unable to vote. Discarding vote.", vote.getAssociateId());
                    return;
                }
            } catch (Exception e) {
                log.error("Error validating user {}: {}. Assuming UNABLE_TO_VOTE.", vote.getAssociateId(), e.getMessage());
                throw e;
            }

            voteRepository.save(vote);
            log.info("Vote saved successfully: {}", vote);
        } catch (DuplicateKeyException e) {
            log.warn("Duplicate vote detected for agenda {} and associate {}. Ignoring.", vote.getAgendaId(), vote.getAssociateId());
        } catch (Exception e) {
            log.error("Error processing vote: {}", vote, e);
            throw e;
        }
    }
}
