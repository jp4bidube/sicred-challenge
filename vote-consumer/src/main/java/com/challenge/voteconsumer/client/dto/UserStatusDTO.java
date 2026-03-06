package com.challenge.voteconsumer.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDTO {
    private String status; // ABLE_TO_VOTE, UNABLE_TO_VOTE
}
