package com.challenge.core.services;

import com.challenge.core.mappers.AgendaMapper;
import com.challenge.core.model.Agenda;
import com.challenge.core.model.AgendaStatus;
import com.challenge.core.model.dto.AgendaResponseDTO;
import com.challenge.core.model.dto.CreateAgendaRequestDTO;
import com.challenge.core.repositories.AgendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;

    public AgendaService(AgendaRepository agendaRepository, AgendaMapper agendaMapper) {
        this.agendaRepository = agendaRepository;
        this.agendaMapper = agendaMapper;
    }

    public AgendaResponseDTO create(CreateAgendaRequestDTO request) {
        Agenda agenda = agendaMapper.toEntity(request);
        Agenda savedAgenda = agendaRepository.save(agenda);
        return agendaMapper.toResponseDTO(savedAgenda);
    }

    public Optional<AgendaResponseDTO> findById(String id) {
        return agendaRepository.findById(id)
                .map(agendaMapper::toResponseDTO);
    }

    public List<AgendaResponseDTO> findAll() {
        return agendaRepository.findAll().stream()
                .map(agendaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<AgendaResponseDTO> openSession(String id, Long minutes) {
        return agendaRepository.findById(id)
                .map(agenda -> {
                    if (agenda.getStatus() == AgendaStatus.CREATED) {
                        agenda.setStatus(AgendaStatus.OPEN);
                        agenda.setSessionEndsAt(LocalDateTime.now().plusMinutes(minutes != null ? minutes : 1));
                        return agendaRepository.save(agenda);
                    }
                    return agenda;
                })
                .map(agendaMapper::toResponseDTO);
    }

    public List<Agenda> findOpenSessionsExpired() {
        return agendaRepository.findByStatusAndSessionEndsAtBefore(AgendaStatus.OPEN, LocalDateTime.now());
    }
}
