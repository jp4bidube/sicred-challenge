package com.challenge.userapi.service;

import com.challenge.userapi.dto.UserStatusDTO;
import com.challenge.userapi.dto.UserVoteStatus;
import com.challenge.userapi.utils.CpfValidator;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserStatusDTO validateCpf(String cpf) {
        // Remove non-numeric characters
        String cleanCpf = cpf.replaceAll("\\D", "");

        if (CpfValidator.isCPF(cleanCpf)) {
            return new UserStatusDTO(UserVoteStatus.ABLE_TO_VOTE);
        } else {
            return new UserStatusDTO(UserVoteStatus.UNABLE_TO_VOTE);
        }
    }
}
