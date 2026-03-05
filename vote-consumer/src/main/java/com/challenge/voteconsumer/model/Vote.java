package com.challenge.voteconsumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "votes")
@CompoundIndexes({
        @CompoundIndex(name = "unique_vote_per_agenda", def = "{'agendaId': 1, 'associateId': 1}", unique = true)
})
public class Vote {

    @Id
    private String id;
    private String agendaId;
    private String associateId;
    private VoteChoice choice;
    private LocalDateTime votedAt;
}
