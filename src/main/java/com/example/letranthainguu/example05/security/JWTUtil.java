package com.example.letranthainguu.example05.security;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.security.core.userdetails.UserDetailsService;

@Component
public class JWTUtil {
    @Value("${jwt.secret}")
    private String secret;

    private final UserDetailsService userDetailsServiceImpl;

    @Autowired
    public JWTUtil(UserDetailsService userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    public String generateToken(String email) throws IllegalArgumentException, JWTCreationException {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
        return JWT.create()
                .withSubject("User Details")
                .withClaim("email", email)
                .withClaim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .withIssuedAt(new Date())
                .withIssuer("Event Scheduler")
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withSubject("User Details")
                    .withIssuer("Event Scheduler")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("JWT Claims: " + jwt.getClaims());
            System.out.println("JWT Roles: " + jwt.getClaim("roles").asList(String.class));
            return jwt.getClaim("email").asString();
        } catch (JWTVerificationException e) {
            System.out.println("JWT Verification failed: " + e.getMessage());
            throw e;
        }
    }
}
