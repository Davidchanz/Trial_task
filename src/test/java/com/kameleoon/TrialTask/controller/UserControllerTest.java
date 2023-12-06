package com.kameleoon.TrialTask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kameleoon.TrialTask.config.SecurityConfig;
import com.kameleoon.TrialTask.config.TestConfig;
import com.kameleoon.TrialTask.dto.LoginDto;
import com.kameleoon.TrialTask.dto.UserAuthDto;
import com.kameleoon.TrialTask.dto.UserDto;
import com.kameleoon.TrialTask.exception.RequiredRequestParamIsMissing;
import com.kameleoon.TrialTask.mapper.UserMapper;
import com.kameleoon.TrialTask.model.CustomUserDetails;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.repository.UserRepository;
import com.kameleoon.TrialTask.service.AuthService;
import com.kameleoon.TrialTask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ UserController.class })
@Import({ SecurityConfig.class, TestConfig.class })
class UserControllerTest extends AbstractTest{

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserMapper userMapper;

    @InjectMocks
    UserController userController;

    @BeforeEach
    public void setUp() {
        super.setUp(userController);
        userService.userRepository = userRepository;
        userService.userMapper = userMapper;
    }
    @Test
    void GetUser_Success() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(new UserDto(user)).replaceAll("[\\t\\s]", "");

        var result = this.mvc.perform(get("/api/user")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(requestJson, result.getResponse().getContentAsString());
    }

    @Test
    void UpdateUser_Success() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        doNothing().when(userService).updateUser(any(String.class), any(UserAuthDto.class));

        UserAuthDto userAuthDto = new UserAuthDto();
        userAuthDto.setUsername("admin");
        userAuthDto.setEmail("admin@email.com");
        userAuthDto.setMatchingPassword("password");
        userAuthDto.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userAuthDto);

        var result = this.mvc.perform(put("/api/user/update")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("User [" + user.getUsername() + "] updated!"));
    }

    @Test
    void UpdateUser_UserAuthDtoParameterMissing_ExceptionThrow() throws Exception {
        this.mvc.perform(put("/api/user/update")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RequiredRequestParamIsMissing))
                .andExpect(result -> assertEquals("Required request param UserAuthDto is missing", result.getResolvedException().getMessage()));

    }

    @Test
    void UpdateUser_UsernameNotFound_ExceptionThrow() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        doReturn(Optional.empty()).when(userRepository).findByUsername(any(String.class));
        doCallRealMethod().when(userService).updateUser(any(String.class), any(UserAuthDto.class));
        doCallRealMethod().when(userService).findUserByUserName(any(String.class));

        UserAuthDto userAuthDto = new UserAuthDto();
        userAuthDto.setUsername("admin");
        userAuthDto.setEmail("admin@email.com");
        userAuthDto.setMatchingPassword("password");
        userAuthDto.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userAuthDto);

        this.mvc.perform(put("/api/user/update")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UsernameNotFoundException))
                .andExpect(result -> assertEquals("Could not found a user with given name", result.getResolvedException().getMessage()));

    }

    @Test
    void UpdateUser_ArgumentNotValid_ExceptionThrow() throws Exception {
        UserAuthDto userAuthDto = new UserAuthDto();
        userAuthDto.setUsername("ad");
        userAuthDto.setEmail("admin.email.com");
        userAuthDto.setPassword("d");
        userAuthDto.setMatchingPassword("pass");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userAuthDto);

        var result = this.mvc.perform(put("/api/user/update")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Password must be between 8 and 25!"));
        assertThat(result.getResponse().getContentAsString(), containsString("Username must be between 5 and 25!"));
        assertThat(result.getResponse().getContentAsString(), containsString("Passwords don't match"));
        assertThat(result.getResponse().getContentAsString(), containsString("Invalid email"));
    }
}