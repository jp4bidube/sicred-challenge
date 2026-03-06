package com.challenge.voteconsumer.repository;

import com.challenge.voteconsumer.model.Vote;
import com.challenge.voteconsumer.model.VoteChoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends MongoRepository<Vote, String> {
    long countByAgendaIdAndChoice(String agendaId, VoteChoice choice);
}
