package com.challenge.core.services;

import com.challenge.core.exception.BadRequestException;
import com.challenge.core.exception.NotFoundException;
import com.challenge.core.mappers.AgendaMapper;
import com.challenge.core.model.Agenda;
import com.challenge.core.model.AgendaStatus;
import com.challenge.core.model.dto.AgendaResponseDTO;
import com.challenge.core.model.dto.CreateAgendaRequestDTO;
import com.challenge.core.repositories.AgendaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;
    private final StringRedisTemplate redisTemplate;

    public AgendaResponseDTO create(CreateAgendaRequestDTO request) {
        Agenda agenda = agendaMapper.toEntity(request);
        Agenda savedAgenda = agendaRepository.save(agenda);
        return agendaMapper.toResponseDTO(savedAgenda);
    }

    public AgendaResponseDTO findById(String id) {
        return agendaRepository.findById(id)
                .map(agendaMapper::toResponseDTO)
                .orElseThrow(() -> new NotFoundException("Agenda not found with id: " + id));
    }

    public List<AgendaResponseDTO> findAll() {
        return agendaRepository.findAll().stream()
                .map(agendaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AgendaResponseDTO openSession(String id, Long minutes) {
        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agenda not found with id: " + id));

        if (agenda.getStatus() == AgendaStatus.OPEN) {
            throw new BadRequestException("Agenda is already open");
        }

        if (agenda.getStatus() == AgendaStatus.CLOSED) {
            throw new BadRequestException("Agenda is already closed");
        }

        long durationMinutes = minutes != null && minutes > 0 ? minutes : 1;
        agenda.setStatus(AgendaStatus.OPEN);
        agenda.setSessionEndsAt(OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(durationMinutes));
        agenda = agendaRepository.save(agenda);

        String key = "agenda:" + id + ":status";
        redisTemplate.opsForValue().set(key, "OPEN", durationMinutes, TimeUnit.MINUTES);
        
        return agendaMapper.toResponseDTO(agenda);
    }

    public List<Agenda> findOpenSessionsExpired() {
        return agendaRepository.findByStatusAndSessionEndsAtBefore(AgendaStatus.OPEN, OffsetDateTime.now(ZoneId.of("America/Sao_Paulo")));
    }
}
