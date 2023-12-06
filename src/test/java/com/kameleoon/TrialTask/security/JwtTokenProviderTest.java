package com.kameleoon.TrialTask.security;

import com.kameleoon.TrialTask.model.CustomUserDetails;
import com.kameleoon.TrialTask.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtTokenProviderTest {

    private static final long jwtExpiryInMs = 25000;

    private JwtTokenProvider tokenProvider;

    @Autowired
    public JwtEncoder encoder;

    @Autowired
    public JwtDecoder decoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.tokenProvider = new JwtTokenProvider(jwtExpiryInMs);
        this.tokenProvider.encoder = encoder;
        this.tokenProvider.decoder = decoder;
    }

    @Test
    void GetUserIdFromJWT_Success() {
        String token = tokenProvider.generateToken(stubCustomUser());
        assertEquals(100L, tokenProvider.getUserIdFromJWT(token).longValue());
    }

    private CustomUserDetails stubCustomUser() {
        User user = new User();
        user.setId(100L);
        return new CustomUserDetails(user);
    }
}
