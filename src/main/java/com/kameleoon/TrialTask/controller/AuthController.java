package com.kameleoon.TrialTask.controller;

import com.kameleoon.TrialTask.dto.ApiResponseSingleOk;
import com.kameleoon.TrialTask.dto.LoginDto;
import com.kameleoon.TrialTask.dto.TokenDto;
import com.kameleoon.TrialTask.dto.UserAuthDto;
import com.kameleoon.TrialTask.exception.RequiredRequestParamIsMissingException;
import com.kameleoon.TrialTask.exception.UserLoginException;
import com.kameleoon.TrialTask.model.CustomUserDetails;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.security.JwtTokenProvider;
import com.kameleoon.TrialTask.service.AuthService;
import com.kameleoon.TrialTask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<?> register(@Valid @RequestBody(required = false) UserAuthDto userAuthDto){
        if(userAuthDto == null)
            throw new RequiredRequestParamIsMissingException("Required request param UserAuthDto is missing");

        User user = new User();
        user.setUsername(userAuthDto.getUsername());
        user.setPassword(userAuthDto.getPassword());
        user.setEmail(userAuthDto.getEmail());
        userService.registerNewUserAccount(user);
        return new ResponseEntity<>(new ApiResponseSingleOk("Registration", "User '" + user.getUsername() + "' successfully created!"), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) LoginDto loginRequest){
        if(loginRequest == null)
            throw new RequiredRequestParamIsMissingException("Required request param LoginDto is missing");

        Authentication authentication = authService.authenticateUser(loginRequest)
                .orElseThrow(() -> new UserLoginException("Couldn't login user [" + loginRequest + "]"));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        System.out.println("Logged in User returned [API]: " + customUserDetails.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = authService.generateToken(customUserDetails);
        TokenDto token = new TokenDto();
        token.setToken(jwtToken);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
