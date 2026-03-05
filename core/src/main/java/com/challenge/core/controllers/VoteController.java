package com.challenge.core.controllers;

import com.challenge.core.model.dto.VoteRequestDTO;
import com.challenge.core.model.dto.VoteResponseDTO;
import com.challenge.core.services.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agendas/{agendaId}/votes")
@Tag(name = "Votação", description = "Registro de votos nas pautas")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    @Operation(summary = "Registrar voto", description = "Envia um voto para processamento assíncrono via Kafka")
    public ResponseEntity<VoteResponseDTO> registerVote(@PathVariable String agendaId, @RequestBody VoteRequestDTO request) {
        VoteResponseDTO response = voteService.registerVote(agendaId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
