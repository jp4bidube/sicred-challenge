package com.challenge.core.controllers;

import com.challenge.core.exception.ErrorResponse;
import com.challenge.core.exception.NotFoundException;
import com.challenge.core.model.VotingResult;
import com.challenge.core.repositories.VotingResultRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/agendas/{agendaId}/result")
@Tag(name = "Resultado", description = "Consulta de resultados das votações")
@AllArgsConstructor
public class VotingResultController {

    private final VotingResultRepository votingResultRepository;

    @GetMapping
    @Operation(summary = "Consultar resultado", description = "Retorna o resultado consolidado da votação de uma pauta")
    public ResponseEntity<VotingResult> getResult(@PathVariable String agendaId) {
        Optional<VotingResult> result = votingResultRepository.findByAgendaId(agendaId);
        return result.map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Resultado ainda não foi gerado para a pauta com ID: " + agendaId));
    }
}
