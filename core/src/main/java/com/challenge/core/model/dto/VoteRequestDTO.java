package com.challenge.core.model.dto;

import com.challenge.core.model.VoteChoice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequestDTO {
    
    @Schema(description = "ID do associado", example = "123456")
    private String associateId;
    
    @Schema(description = "Opção de voto", example = "YES", allowableValues = {"YES", "NO"})
    private VoteChoice choice;
}
