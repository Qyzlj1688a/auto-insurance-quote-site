package com.example.quote.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String testSecret = "Test_Secret_Key_For_Jwt_Provider_Token_Test";
    private final long testExpirationMs = 5000; // 5 seconds for normal tests

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(testSecret, testExpirationMs);
    }

    @Test
    void generateAndValidateTokenSuccessForStandardUser() {
        String username = "admin";
        String token = tokenProvider.generateToken(username);
        assertNotNull(token);

        String validatedUsername = tokenProvider.validateTokenAndGetUsername(token);
        assertEquals(username, validatedUsername);
    }

    @Test
    void generateAndValidateTokenSuccessForUsernameWithDots() {
        // Test username with dots (to verify dot username split fix)
        String username = "admin.ts.developer.2026";
        String token = tokenProvider.generateToken(username);
        assertNotNull(token);

        String validatedUsername = tokenProvider.validateTokenAndGetUsername(token);
        assertEquals(username, validatedUsername);
    }

    @Test
    void validateTokenReturnsNullWhenExpired() throws InterruptedException {
        // Use a very short expiration time (1ms)
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(testSecret, 1);
        String token = shortLivedProvider.generateToken("admin");
        
        // Wait 5ms to guarantee expiration
        Thread.sleep(5);

        String validatedUsername = shortLivedProvider.validateTokenAndGetUsername(token);
        assertNull(validatedUsername);
    }

    @Test
    void validateTokenReturnsNullWhenSignatureIsTampered() {
        String token = tokenProvider.generateToken("admin");
        
        // Tamper signature by appending a character
        String tamperedToken = token + "A";
        
        String validatedUsername = tokenProvider.validateTokenAndGetUsername(tamperedToken);
        assertNull(validatedUsername);
    }
}
