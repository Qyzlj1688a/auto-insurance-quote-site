package com.example.quote.service;

import com.example.quote.dto.request.AdminLoginRequest;
import com.example.quote.dto.response.AdminLoginResponse;

/**
 * Administrator authentication service contract.
 */
public interface AdminAuthService {

    AdminLoginResponse login(AdminLoginRequest request);
}
