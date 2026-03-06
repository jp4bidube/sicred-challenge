package com.challenge.voteconsumer.service;

import com.challenge.voteconsumer.model.Vote;
import com.challenge.voteconsumer.repository.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class VoteConsumerService {

    private static final Logger log = LoggerFactory.getLogger(VoteConsumerService.class);
    private final VoteRepository voteRepository;

    public VoteConsumerService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @KafkaListener(topics = "votes", groupId = "vote-consumer-group")
    public void consumeVote(Vote vote) {
        log.info("Received vote: {}", vote);
        try {
            voteRepository.save(vote);
            log.info("Vote saved successfully: {}", vote);
        } catch (DuplicateKeyException e) {
            log.warn("Duplicate vote detected for agenda {} and associate {}. Ignoring.", vote.getAgendaId(), vote.getAssociateId());
        } catch (Exception e) {
            log.error("Error saving vote: {}", vote, e);
            throw e;
        }
    }
}
