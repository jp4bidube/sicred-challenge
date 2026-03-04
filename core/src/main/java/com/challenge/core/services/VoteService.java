package com.challenge.core.services;

import com.challenge.core.model.Vote;
import com.challenge.core.model.dto.VoteRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class VoteService {

    private final VoteProducer voteProducer;

    public VoteService(VoteProducer voteProducer) {
        this.voteProducer = voteProducer;
    }

    public void registerVote(String agendaId, VoteRequestDTO request) {
        Vote vote = Vote.builder()
                .agendaId(agendaId)
                .associateId(request.getAssociateId())
                .choice(request.getChoice())
                .build();
        
        voteProducer.sendVote(vote);
    }
}
