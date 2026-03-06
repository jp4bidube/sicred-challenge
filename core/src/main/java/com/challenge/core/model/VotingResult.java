package com.challenge.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "voting_results")
public class VotingResult {

    @Id
    private String id;
    private String agendaId;
    private long votesYes;
    private long votesNo;
    private String result; // "APPROVED", "REJECTED", "TIED"
}
