package com.challenge.voteconsumer.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotingResultCalculatedEvent {
    private String agendaId;
    private long votesYes;
    private long votesNo;
}
