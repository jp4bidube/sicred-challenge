package com.challenge.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "agendas")
public class Agenda {

    @Id
    private String id;
    private String title;
    private AgendaStatus status;
    private LocalDateTime sessionEndsAt;

    public Agenda(String title) {
        this.title = title;
        this.status = AgendaStatus.CREATED;
    }
}
