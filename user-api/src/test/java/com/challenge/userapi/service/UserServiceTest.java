package com.challenge.userapi.service;

import com.challenge.userapi.dto.UserStatusDTO;
import com.challenge.userapi.dto.UserVoteStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Test
    void validateCpf_ShouldReturnAbleToVote_WhenCpfIsValid() {
        // CPF válido gerado para teste (não é real)
        String validCpf = "52998224725"; 
        
        UserStatusDTO result = userService.validateCpf(validCpf);

        assertNotNull(result);
        assertEquals(UserVoteStatus.ABLE_TO_VOTE, result.getStatus());
    }

    @Test
    void validateCpf_ShouldReturnUnableToVote_WhenCpfIsInvalid() {
        String invalidCpf = "12345678900";

        UserStatusDTO result = userService.validateCpf(invalidCpf);

        assertNotNull(result);
        assertEquals(UserVoteStatus.UNABLE_TO_VOTE, result.getStatus());
    }

    @Test
    void validateCpf_ShouldReturnUnableToVote_WhenCpfHasInvalidLength() {
        String invalidCpf = "123";

        UserStatusDTO result = userService.validateCpf(invalidCpf);

        assertNotNull(result);
        assertEquals(UserVoteStatus.UNABLE_TO_VOTE, result.getStatus());
    }
    
    @Test
    void validateCpf_ShouldHandleFormattedCpf() {
        // CPF válido com formatação
        String validCpf = "529.982.247-25"; 
        
        UserStatusDTO result = userService.validateCpf(validCpf);

        assertNotNull(result);
        assertEquals(UserVoteStatus.ABLE_TO_VOTE, result.getStatus());
    }
}
