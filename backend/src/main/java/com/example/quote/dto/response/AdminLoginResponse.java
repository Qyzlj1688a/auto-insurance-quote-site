package com.example.quote.dto.response;

/**
 * 管理者ログイン成功時のレスポンス情報（トークンおよびユーザー表示名）を保持するDTOクラス。
 */
public class AdminLoginResponse {

    private String loginId;
    private String displayName;
    private String message;
    private String token;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
