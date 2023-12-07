package com.kameleoon.TrialTask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kameleoon.TrialTask.config.SecurityConfig;
import com.kameleoon.TrialTask.config.TestConfig;
import com.kameleoon.TrialTask.dto.LoginDto;
import com.kameleoon.TrialTask.dto.UserAuthDto;
import com.kameleoon.TrialTask.exception.EmailAlreadyExistException;
import com.kameleoon.TrialTask.exception.RequiredRequestParamIsMissingException;
import com.kameleoon.TrialTask.exception.UserAlreadyExistException;
import com.kameleoon.TrialTask.exception.UserLoginException;
import com.kameleoon.TrialTask.model.CustomUserDetails;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.service.AuthService;
import com.kameleoon.TrialTask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ AuthController.class })
@Import({ SecurityConfig.class, TestConfig.class })
public class AuthControllerTest extends AbstractTest{

    @MockBean
    AuthService authService;

    @MockBean
    UserService userService;

    @MockBean
    Authentication authentication;

    @InjectMocks
    AuthController authController;

    @BeforeEach
    public void setUp() {
        super.setUp(authController);
    }

    @Test
    void Login_Success() throws Exception {
        when(authService.authenticateUser(any(LoginDto.class))).thenReturn(Optional.ofNullable(authentication));
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(authService.generateToken(any(CustomUserDetails.class))).thenReturn(jwtTokenProvider.generateToken(customUserDetails));

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("admin");
        loginDto.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(loginDto);

        MvcResult result = this.mvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void Login_CantLoginUser_ExceptionThrow() throws Exception {
        when(authService.authenticateUser(any(LoginDto.class))).thenReturn(Optional.empty());

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("admin");
        loginDto.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(loginDto);

        this.mvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserLoginException))
                .andExpect(result -> assertEquals("Couldn't login user [" + loginDto + "]", result.getResolvedException().getMessage()));
    }

    @Test
    void Login_LoginDtoParameterMissing_ExceptionThrow() throws Exception {
        this.mvc.perform(post("/api/auth/login"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RequiredRequestParamIsMissingException))
                .andExpect(result -> assertEquals("Required request param LoginDto is missing", result.getResolvedException().getMessage()));
    }

    @Test
    void Register_Success() throws Exception {
        UserAuthDto userAuthDto = new UserAuthDto();
        userAuthDto.setEmail("admin@email.com");
        userAuthDto.setUsername("admin");
        userAuthDto.setMatchingPassword("password");
        userAuthDto.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userAuthDto);

        MvcResult result = this.mvc.perform(post("/api/auth/registration")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void Register_UserDtoParameterMissing_ExceptionThrow() throws Exception {
        this.mvc.perform(post("/api/auth/registration"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RequiredRequestParamIsMissingException))
                .andExpect(result -> assertEquals("Required request param UserAuthDto is missing", result.getResolvedException().getMessage()));
    }

    @Test
    void Register_UserNameAlreadyExist_ExceptionThrow() throws Exception {

        doCallRealMethod().when(userService).registerNewUserAccount(any(User.class));
        when(userService.userExists(any(String.class))).thenReturn(true);

        UserAuthDto userAuthDto = new UserAuthDto();
        userAuthDto.setUsername("admin");
        userAuthDto.setEmail("admin@email.com");
        userAuthDto.setMatchingPassword("password");
        userAuthDto.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userAuthDto);

        this.mvc.perform(post("/api/auth/registration")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistException))
                .andExpect(result -> assertEquals("User with username: '"
                        + userAuthDto.getUsername() + "' is already exist!", result.getResolvedException().getMessage()));

    }

    @Test
    void Register_EmailAlreadyExist_ExceptionThrow() throws Exception {

        doCallRealMethod().when(userService).registerNewUserAccount(any(User.class));
        when(userService.emailExists(any(String.class))).thenReturn(true);

        UserAuthDto userAuthDto = new UserAuthDto();
        userAuthDto.setUsername("admin");
        userAuthDto.setEmail("admin@email.com");
        userAuthDto.setMatchingPassword("password");
        userAuthDto.setPassword("password");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userAuthDto);

        this.mvc.perform(post("/api/auth/registration")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EmailAlreadyExistException))
                .andExpect(result -> assertEquals("User with email: '"
                        + userAuthDto.getEmail() + "' is already exist!", result.getResolvedException().getMessage()));

    }

    @Test
    void Register_ArgumentNotValid_ExceptionThrow() throws Exception {
        UserAuthDto userAuthDto = new UserAuthDto();
        userAuthDto.setUsername("ad");
        userAuthDto.setEmail("admin.email.com");
        userAuthDto.setPassword("d");
        userAuthDto.setMatchingPassword("pass");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(userAuthDto);

        var result = this.mvc.perform(post("/api/auth/registration")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Password must be between 8 and 25!"));
        assertThat(result.getResponse().getContentAsString(), containsString("Username must be between 5 and 25!"));
        assertThat(result.getResponse().getContentAsString(), containsString("Passwords don't match"));
        assertThat(result.getResponse().getContentAsString(), containsString("Invalid email"));
    }
}
