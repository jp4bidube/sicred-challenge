package com.challenge.voteconsumer.client;

import com.challenge.voteconsumer.client.dto.UserStatusDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-api", url = "${user.api.url}")
public interface UserClient {

    @GetMapping("/v1/users/{cpf}")
    UserStatusDTO checkVoteStatus(@PathVariable("cpf") String cpf);
}
