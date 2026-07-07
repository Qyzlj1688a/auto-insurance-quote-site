package com.example.quote.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Lightweight stateless Token provider using HmacSHA256 algorithm.
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
        long expiry = System.currentTimeMillis() + expirationTime;
        String payload = username + "." + expiry;
        String signature = sign(payload);
        return Base64.getUrlEncoder().encodeToString((payload + "." + signature).getBytes(StandardCharsets.UTF_8));
    }

    public String validateTokenAndGetUsername(String token) {
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(token);
            String decoded = new String(decodedBytes, StandardCharsets.UTF_8);

            // Locate the last dot separating signature
            int lastDot = decoded.lastIndexOf('.');
            if (lastDot == -1) {
                return null;
            }
            String signature = decoded.substring(lastDot + 1);
            String payload = decoded.substring(0, lastDot);

            // Locate the second to last dot separating expiry
            int secondLastDot = payload.lastIndexOf('.');
            if (secondLastDot == -1) {
                return null;
            }
            String username = payload.substring(0, secondLastDot);
            String expiryStr = payload.substring(secondLastDot + 1);

            long expiry = Long.parseLong(expiryStr);
            if (System.currentTimeMillis() > expiry) {
                return null; // Token expired
            }

            String expectedSignature = sign(username + "." + expiry);
            if (MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8))) {
                return username;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private String sign(String data) {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] hash = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC signing failed", e);
        }
    }
}
