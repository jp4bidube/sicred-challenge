package com.challenge.core.services;

import com.challenge.core.exception.BadRequestException;
import com.challenge.core.model.dto.VoteRequestDTO;
import com.challenge.core.model.dto.VoteResponseDTO;
import com.challenge.core.model.events.RegisterVoteEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
            throw new BadRequestException("Voting session is closed or does not exist for agenda: " + agendaId);
        }

        RegisterVoteEvent event = RegisterVoteEvent.builder()
                .agendaId(agendaId)
                .associateId(request.getAssociateId())
                .choice(request.getChoice())
                .votedAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        voteProducer.sendVote(event);

        return VoteResponseDTO.builder()
                .agendaId(event.getAgendaId())
                .associateId(event.getAssociateId())
                .choice(event.getChoice())
                .votedAt(event.getVotedAt())
                .build();
    }
}
