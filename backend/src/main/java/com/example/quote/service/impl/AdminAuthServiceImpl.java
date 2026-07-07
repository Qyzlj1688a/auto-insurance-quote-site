package com.example.quote.service.impl;

import com.example.quote.dto.request.AdminLoginRequest;
import com.example.quote.dto.response.AdminLoginResponse;
import com.example.quote.entity.AdminUser;
import com.example.quote.exception.BusinessException;
import com.example.quote.repository.AdminUserRepository;
import com.example.quote.security.JwtTokenProvider;
import com.example.quote.service.AdminAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Administrator authentication service implementation.
 */
@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AdminAuthServiceImpl(
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider
    ) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        AdminUser adminUser = adminUserRepository.findByLoginIdAndActiveTrue(request.getLoginId())
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.UNAUTHORIZED,
                        "UNAUTHORIZED",
                        "ログインIDまたはパスワードが正しくありません。"
                ));

        if (!passwordEncoder.matches(request.getPassword(), adminUser.getPasswordHash())) {
            throw new BusinessException(
                    HttpStatus.UNAUTHORIZED,
                    "UNAUTHORIZED",
                    "ログインIDまたはパスワードが正しくありません。"
            );
        }

        String token = tokenProvider.generateToken(adminUser.getLoginId());

        AdminLoginResponse response = new AdminLoginResponse();
        response.setLoginId(adminUser.getLoginId());
        response.setDisplayName(adminUser.getDisplayName());
        response.setMessage("ログインに成功しました。");
        response.setToken(token);
        return response;
    }
}
