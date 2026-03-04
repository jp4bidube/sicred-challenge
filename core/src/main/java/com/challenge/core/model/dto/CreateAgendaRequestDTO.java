package com.challenge.core.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgendaRequestDTO {
    
    @NotBlank(message = "Title cannot be empty or null")
    private String title;
}
