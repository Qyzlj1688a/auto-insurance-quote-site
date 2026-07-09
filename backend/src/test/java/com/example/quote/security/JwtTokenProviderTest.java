package com.example.quote.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String testSecret = "Test_Secret_Key_For_Jwt_Provider_Token_Test";
    private final long testExpirationMs = 5000; // 通常テスト用の有効期限（5秒）

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(testSecret, testExpirationMs);
    }

    @Test
    void generateAndValidateTokenSuccessForStandardUser() {
        String username = "admin";
        String token = tokenProvider.generateToken(username);
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);

        String validatedUsername = tokenProvider.validateTokenAndGetUsername(token);
        assertEquals(username, validatedUsername);
    }

    @Test
    void generateAndValidateTokenSuccessForUsernameWithDots() {
        // ドットを含むユーザー名でもJWTのsubjectとして正しく扱えることを確認する
        String username = "admin.ts.developer.2026";
        String token = tokenProvider.generateToken(username);
        assertNotNull(token);

        String validatedUsername = tokenProvider.validateTokenAndGetUsername(token);
        assertEquals(username, validatedUsername);
    }

    @Test
    void validateTokenReturnsNullWhenExpired() throws InterruptedException {
        // 有効期限切れを確認するため、極端に短い有効期限（1ms）を設定する
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(testSecret, 1);
        String token = shortLivedProvider.generateToken("admin");
        
        // 期限切れを確実にするため5ms待機する
        Thread.sleep(5);

        String validatedUsername = shortLivedProvider.validateTokenAndGetUsername(token);
        assertNull(validatedUsername);
    }

    @Test
    void validateTokenReturnsNullWhenSignatureIsTampered() {
        String token = tokenProvider.generateToken("admin");
        
        // 末尾に文字を追加して署名不正のJWTに改ざんする
        String tamperedToken = token + "A";
        
        String validatedUsername = tokenProvider.validateTokenAndGetUsername(tamperedToken);
        assertNull(validatedUsername);
    }
}
