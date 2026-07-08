package com.example.quote.service;

import com.example.quote.dto.request.AdminLoginRequest;
import com.example.quote.dto.response.AdminLoginResponse;

/**
 * 管理者認証サービスインターフェース。
 */
public interface AdminAuthService {

    AdminLoginResponse login(AdminLoginRequest request);
}
