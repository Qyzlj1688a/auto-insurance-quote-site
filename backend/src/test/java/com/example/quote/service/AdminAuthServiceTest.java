package com.example.quote.service;

import com.example.quote.dto.request.AdminLoginRequest;
import com.example.quote.dto.response.AdminLoginResponse;
import com.example.quote.entity.AdminUser;
import com.example.quote.exception.BusinessException;
import com.example.quote.repository.AdminUserRepository;
import com.example.quote.service.impl.AdminAuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminAuthServiceTest {

    private final AdminUserRepository adminUserRepository = mock(AdminUserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final com.example.quote.security.JwtTokenProvider tokenProvider = mock(com.example.quote.security.JwtTokenProvider.class);
    private final AdminAuthService adminAuthService = new AdminAuthServiceImpl(adminUserRepository, passwordEncoder, tokenProvider);

    @Test
    void loginReturnsResponseWhenCredentialsAreValid() {
        AdminUser adminUser = new AdminUser();
        adminUser.setLoginId("admin");
        adminUser.setDisplayName("管理者");
        adminUser.setPasswordHash(passwordEncoder.encode("Admin123!"));
        adminUser.setActive(true);

        when(adminUserRepository.findByLoginIdAndActiveTrue("admin")).thenReturn(Optional.of(adminUser));
        when(tokenProvider.generateToken("admin")).thenReturn("mocked_jwt_token");

        AdminLoginRequest request = new AdminLoginRequest();
        request.setLoginId("admin");
        request.setPassword("Admin123!");

        AdminLoginResponse response = adminAuthService.login(request);

        assertEquals("admin", response.getLoginId());
        assertEquals("管理者", response.getDisplayName());
        assertEquals("ログインに成功しました。", response.getMessage());
        assertEquals("mocked_jwt_token", response.getToken());
    }

    @Test
    void loginThrowsUnauthorizedWhenPasswordIsInvalid() {
        AdminUser adminUser = new AdminUser();
        adminUser.setLoginId("admin");
        adminUser.setDisplayName("管理者");
        adminUser.setPasswordHash(passwordEncoder.encode("Admin123!"));
        adminUser.setActive(true);

        when(adminUserRepository.findByLoginIdAndActiveTrue("admin")).thenReturn(Optional.of(adminUser));

        AdminLoginRequest request = new AdminLoginRequest();
        request.setLoginId("admin");
        request.setPassword("wrong-password");

        BusinessException exception = assertThrows(BusinessException.class, () -> adminAuthService.login(request));

        assertEquals("UNAUTHORIZED", exception.getCode());
        assertEquals("ログインIDまたはパスワードが正しくありません。", exception.getMessage());
    }
}
