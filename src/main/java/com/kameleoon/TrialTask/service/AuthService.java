package com.kameleoon.TrialTask.service;

import com.kameleoon.TrialTask.dto.LoginDto;
import com.kameleoon.TrialTask.model.CustomUserDetails;
import com.kameleoon.TrialTask.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public Optional<Authentication> authenticateUser(LoginDto loginRequest) {
        try{
            return Optional.ofNullable(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                    loginRequest.getPassword())));
        }catch (AuthenticationException ex){
            return Optional.empty();
        }
    }

    public String generateToken(CustomUserDetails customUserDetails) {
        return tokenProvider.generateToken(customUserDetails);
    }

}
