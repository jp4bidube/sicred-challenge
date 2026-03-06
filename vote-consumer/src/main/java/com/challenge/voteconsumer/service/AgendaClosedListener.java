package com.challenge.voteconsumer.service;

import com.challenge.voteconsumer.model.VoteChoice;
import com.challenge.voteconsumer.model.events.AgendaClosedEvent;
import com.challenge.voteconsumer.model.events.VotingResultCalculatedEvent;
import com.challenge.voteconsumer.repository.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AgendaClosedListener {

    private static final Logger log = LoggerFactory.getLogger(AgendaClosedListener.class);
    private final VoteRepository voteRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AgendaClosedListener(VoteRepository voteRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.voteRepository = voteRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "agenda-closed", groupId = "vote-consumer-group")
    public void handleAgendaClosed(AgendaClosedEvent event) {
        log.info("Received AgendaClosedEvent for agenda: {}", event.getAgendaId());

        long votesYes = voteRepository.countByAgendaIdAndChoice(event.getAgendaId(), VoteChoice.YES);
        long votesNo = voteRepository.countByAgendaIdAndChoice(event.getAgendaId(), VoteChoice.NO);

        log.info("Voting result for agenda {}: YES={}, NO={}", event.getAgendaId(), votesYes, votesNo);

        VotingResultCalculatedEvent resultEvent = VotingResultCalculatedEvent.builder()
                .agendaId(event.getAgendaId())
                .votesYes(votesYes)
                .votesNo(votesNo)
                .build();

        try {
            kafkaTemplate.send("voting-result", resultEvent);
            log.info("VotingResultCalculatedEvent sent for agenda: {}", event.getAgendaId());
        } catch (Exception e) {
            log.error("Error sending VotingResultCalculatedEvent for agenda: {}", event.getAgendaId(), e);
        }
    }
}
