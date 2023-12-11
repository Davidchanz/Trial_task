package com.kameleoon.TrialTask.security;

import com.kameleoon.TrialTask.controller.UserController;
import com.kameleoon.TrialTask.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ UserController.class })
@AutoConfigureMockMvc
public class UnAuthorizedTest {

    @MockBean
    UserService userService;
    @Autowired
    public MockMvc mvc;

    @Test
    void NeedAuth_ExceptionThrow() throws Exception{
        this.mvc.perform(get("/api/user"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
