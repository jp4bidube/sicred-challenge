package com.challenge.core.services;

import com.challenge.core.model.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class VoteProducer {

    private static final Logger log = LoggerFactory.getLogger(VoteProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public VoteProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendVote(Vote vote) {
        try {
            this.kafkaTemplate.send("votes", vote);
            log.info("Vote sent successfully: {}", vote);
        } catch (Exception e) {
            log.error("Error sending vote: {}", vote, e);
        }
    }
}
