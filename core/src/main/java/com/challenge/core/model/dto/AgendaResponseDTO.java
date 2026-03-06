package com.challenge.core.model.dto;

import com.challenge.core.model.Agenda;
import com.challenge.core.model.AgendaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaResponseDTO {

    private String id;
    private String title;
    private AgendaStatus status;
    private OffsetDateTime sessionEndsAt;

    public static AgendaResponseDTO fromEntity(Agenda agenda) {
        return AgendaResponseDTO.builder()
                .id(agenda.getId())
                .title(agenda.getTitle())
                .status(agenda.getStatus())
                .sessionEndsAt(agenda.getSessionEndsAt())
                .build();
    }
}
