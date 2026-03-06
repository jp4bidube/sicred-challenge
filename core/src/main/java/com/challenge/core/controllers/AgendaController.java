package com.challenge.core.controllers;

import com.challenge.core.model.dto.AgendaResponseDTO;
import com.challenge.core.model.dto.CreateAgendaRequestDTO;
import com.challenge.core.services.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/agendas")
@Tag(name = "Agenda", description = "Gerenciamento de Pautas")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @PostMapping
    @Operation(summary = "Criar uma nova pauta", description = "Cria uma pauta com o título fornecido e status CREATED")
    public ResponseEntity<AgendaResponseDTO> create(@RequestBody @Valid CreateAgendaRequestDTO request) {
        AgendaResponseDTO agenda = agendaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(agenda);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID", description = "Retorna os detalhes de uma pauta específica")
    public ResponseEntity<AgendaResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(agendaService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar todas as pautas", description = "Retorna uma lista com todas as pautas cadastradas")
    public ResponseEntity<List<AgendaResponseDTO>> findAll() {
        return ResponseEntity.ok(agendaService.findAll());
    }

    @PostMapping("/{id}/open")
    @Operation(summary = "Abrir sessão de votação", description = "Abre a sessão de votação para uma pauta por um tempo determinado (padrão 1 minuto)")
    public ResponseEntity<AgendaResponseDTO> openSession(@PathVariable String id, @RequestParam(required = false) Long minutes) {
        return ResponseEntity.ok(agendaService.openSession(id, minutes));
    }
}
