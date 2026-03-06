package com.challenge.core.repositories;

import com.challenge.core.model.Agenda;
import com.challenge.core.model.AgendaStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AgendaRepository extends MongoRepository<Agenda, String> {
    List<Agenda> findByStatusAndSessionEndsAtBefore(AgendaStatus status, OffsetDateTime dateTime);
}
