package com.challenge.core.mappers;

import com.challenge.core.model.Agenda;
import com.challenge.core.model.dto.AgendaResponseDTO;
import com.challenge.core.model.dto.CreateAgendaRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AgendaMapper {

    AgendaResponseDTO toResponseDTO(Agenda agenda);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "votesYes", constant = "0")
    @Mapping(target = "votesNo", constant = "0")
    @Mapping(target = "sessionEndsAt", ignore = true)
    Agenda toEntity(CreateAgendaRequestDTO request);
}
