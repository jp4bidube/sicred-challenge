package com.challenge.userapi.controller;

import com.challenge.userapi.dto.UserStatusDTO;
import com.challenge.userapi.dto.UserVoteStatus;
import com.challenge.userapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void checkVoteStatus_ShouldReturnOk_WhenUserIsAbleToVote() throws Exception {
        String cpf = "12345678909";
        UserStatusDTO statusDTO = new UserStatusDTO(UserVoteStatus.ABLE_TO_VOTE);

        when(userService.validateCpf(cpf)).thenReturn(statusDTO);

        mockMvc.perform(get("/users/{cpf}", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABLE_TO_VOTE"));
    }

    @Test
    void checkVoteStatus_ShouldReturnNotFound_WhenUserIsUnableToVote() throws Exception {
        String cpf = "12345678900";
        UserStatusDTO statusDTO = new UserStatusDTO(UserVoteStatus.UNABLE_TO_VOTE);

        when(userService.validateCpf(cpf)).thenReturn(statusDTO);

        mockMvc.perform(get("/users/{cpf}", cpf))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("UNABLE_TO_VOTE"));
    }
}
