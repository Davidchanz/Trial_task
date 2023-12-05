package com.kameleoon.TrialTask.security;

import com.kameleoon.TrialTask.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_CLAIM = "authorities";
    private final long jwtExpirationInMs;

    @Autowired
    public JwtEncoder encoder;

    @Autowired
    public JwtDecoder decoder;

    public JwtTokenProvider(@Value("${app.jwt.expiration}") long jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    /**
     * Generates a token from a principal object. Embed the refresh token in the jwt
     * so that a new jwt can be created
     */
    public String generateToken(CustomUserDetails customUserDetails) {
        Instant expiryDate = Instant.now().plusMillis(jwtExpirationInMs);
        String authorities = getUserAuthorities(customUserDetails);

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiryDate)
                .subject(Long.toString(customUserDetails.getId()))
                .claim(AUTHORITIES_CLAIM, authorities)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    /**
     * Returns the user id encapsulated within the token
     */
    public Long getUserIdFromJWT(String token) {
        return Long.parseLong(this.decoder.decode(token).getSubject());
    }

    /**
     * Return the jwt authorities claim encapsulated within the token
     */
    public List<GrantedAuthority> getAuthoritiesFromJWT(String token) {
        var claims = this.decoder.decode(token).getClaims();
        return Arrays.stream(claims.get(AUTHORITIES_CLAIM).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Private helper method to extract user authorities.
     */
    private String getUserAuthorities(CustomUserDetails customUserDetails) {
        return customUserDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

}