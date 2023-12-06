package com.kameleoon.TrialTask.config;

import com.kameleoon.TrialTask.security.JwtTokenProvider;
import com.kameleoon.TrialTask.security.JwtTokenValidator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider(){
        return new JwtTokenProvider(12000);
    }

    @Bean
    public JwtTokenValidator jwtTokenValidator(){
        return new JwtTokenValidator();
    }
}
