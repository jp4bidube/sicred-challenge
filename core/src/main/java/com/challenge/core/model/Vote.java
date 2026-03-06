package com.challenge.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    private String id;
    private String agendaId;
    private String associateId;
    private VoteChoice choice;
    private LocalDateTime votedAt;
}
