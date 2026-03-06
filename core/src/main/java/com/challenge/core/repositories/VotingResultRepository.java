package com.challenge.core.repositories;

import com.challenge.core.model.VotingResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VotingResultRepository extends MongoRepository<VotingResult, String> {
    Optional<VotingResult> findByAgendaId(String agendaId);
}
