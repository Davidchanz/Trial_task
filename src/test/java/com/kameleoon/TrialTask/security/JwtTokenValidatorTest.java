package com.kameleoon.TrialTask.security;

import com.kameleoon.TrialTask.exception.InvalidTokenRequestException;
import com.kameleoon.TrialTask.model.CustomUserDetails;
import com.kameleoon.TrialTask.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("TEST")
class JwtTokenValidatorTest {
    private static final long jwtExpiryInMs = 2500;

    private JwtTokenProvider tokenProvider;

    private JwtTokenValidator tokenValidator;

    @Autowired
    public JwtEncoder encoder;

    @Autowired
    public JwtDecoder decoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.tokenProvider = new JwtTokenProvider(jwtExpiryInMs);
        this.tokenValidator = new JwtTokenValidator();
        this.tokenProvider.encoder = encoder;
        this.tokenProvider.decoder = decoder;
        this.tokenValidator.decoder = decoder;
    }

    @Test
    void testValidateTokenThrowsExceptionWhenTokenIsDamaged() {
        String token = tokenProvider.generateToken(stubCustomUser());

        InvalidTokenRequestException ex = assertThrows(InvalidTokenRequestException.class,
                () -> tokenValidator.validateToken(token + "-Damage"));
        assertTrue(ex.getMessage().contains("Invalid signature"));
    }

    private CustomUserDetails stubCustomUser() {
        User user = new User();
        user.setId((long) 100);
        return new CustomUserDetails(user);
    }
}
