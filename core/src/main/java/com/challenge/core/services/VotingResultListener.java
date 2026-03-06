package com.challenge.core.services;

import com.challenge.core.model.VotingResult;
import com.challenge.core.model.events.VotingResultCalculatedEvent;
import com.challenge.core.repositories.VotingResultRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VotingResultListener {

    private static final Logger log = LoggerFactory.getLogger(VotingResultListener.class);
    private final VotingResultRepository votingResultRepository;

    @KafkaListener(topics = "voting-result", groupId = "core-api-group")
    public void handleVotingResult(VotingResultCalculatedEvent event) {
        log.info("Received VotingResultCalculatedEvent for agenda: {}", event.getAgendaId());

        String result;
        if (event.getVotesYes() > event.getVotesNo()) {
            result = "APPROVED";
        } else if (event.getVotesNo() > event.getVotesYes()) {
            result = "REJECTED";
        } else {
            result = "TIED";
        }

        VotingResult votingResult = VotingResult.builder()
                .agendaId(event.getAgendaId())
                .votesYes(event.getVotesYes())
                .votesNo(event.getVotesNo())
                .result(result)
                .build();

        votingResultRepository.save(votingResult);
        log.info("Voting result saved for agenda: {}", event.getAgendaId());
    }
}
