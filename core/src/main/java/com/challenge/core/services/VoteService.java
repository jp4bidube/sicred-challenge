package com.challenge.core.services;

import com.challenge.core.exception.NotFoundException;
import com.challenge.core.model.Vote;
import com.challenge.core.model.dto.VoteRequestDTO;
import com.challenge.core.model.dto.VoteResponseDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VoteService {

    private final VoteProducer voteProducer;
    private final StringRedisTemplate redisTemplate;

    public VoteService(VoteProducer voteProducer, StringRedisTemplate redisTemplate) {
        this.voteProducer = voteProducer;
        this.redisTemplate = redisTemplate;
    }

    public VoteResponseDTO registerVote(String agendaId, VoteRequestDTO request) {
        String key = "agenda:" + agendaId + ":status";
        String status = redisTemplate.opsForValue().get(key);

        if (!"OPEN".equals(status)) {
            throw new IllegalStateException("Voting session is closed or does not exist for agenda: " + agendaId);
        }

        Vote vote = Vote.builder()
                .agendaId(agendaId)
                .associateId(request.getAssociateId())
                .choice(request.getChoice())
                .votedAt(LocalDateTime.now())
                .build();
        
        voteProducer.sendVote(vote);

        return VoteResponseDTO.builder()
                .agendaId(vote.getAgendaId())
                .associateId(vote.getAssociateId())
                .choice(vote.getChoice())
                .votedAt(vote.getVotedAt())
                .build();
    }
}
