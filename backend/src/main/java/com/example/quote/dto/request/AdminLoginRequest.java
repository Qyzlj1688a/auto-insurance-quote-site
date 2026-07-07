package com.example.quote.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Administrator login request.
 */
public class AdminLoginRequest {

    @NotBlank(message = "loginIdは必須です。")
    private String loginId;

    @NotBlank(message = "passwordは必須です。")
    private String password;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
