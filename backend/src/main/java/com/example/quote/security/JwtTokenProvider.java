package com.example.quote.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

/**
 * 標準JWT（HS256）を使用した管理者認証用トークンプロバイダー。
 */
@Component
public class JwtTokenProvider {

    private final String secret;
    private final long expirationTime;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationTime
    ) {
        this.secret = secret;
        this.expirationTime = expirationTime;
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationTime);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String validateTokenAndGetUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
