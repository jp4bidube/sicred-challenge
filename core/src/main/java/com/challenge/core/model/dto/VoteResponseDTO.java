package com.challenge.core.model.dto;

import com.challenge.core.model.VoteChoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponseDTO {
    private String agendaId;
    private String associateId;
    private VoteChoice choice;
    private LocalDateTime votedAt;
}
