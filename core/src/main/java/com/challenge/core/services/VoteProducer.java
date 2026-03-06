package com.challenge.core.services;

import com.challenge.core.model.events.RegisterVoteEvent;
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

    public void sendVote(RegisterVoteEvent event) {
        try {
            this.kafkaTemplate.send("votes", event);
            log.info("Vote event sent successfully: {}", event);
        } catch (Exception e) {
            log.error("Error sending vote event: {}", event, e);
        }
    }
}
