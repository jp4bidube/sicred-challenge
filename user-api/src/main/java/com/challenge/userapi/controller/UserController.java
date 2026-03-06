package com.challenge.userapi.controller;

import com.challenge.userapi.dto.UserStatusDTO;
import com.challenge.userapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "Usuários", description = "Validação de usuários")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{cpf}")
    @Operation(summary = "Verificar status de voto", description = "Verifica se um CPF é válido e está apto a votar")
    public ResponseEntity<UserStatusDTO> checkVoteStatus(@PathVariable String cpf) {
        UserStatusDTO status = userService.validateCpf(cpf);
        
        if (status.getStatus() == com.challenge.userapi.dto.UserVoteStatus.UNABLE_TO_VOTE) {
            return ResponseEntity.status(404).body(status);
        }
        
        return ResponseEntity.ok(status);
    }
}
