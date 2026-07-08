package com.example.quote.dto.response;

import java.util.ArrayList;
import java.util.List;

/**
 * APIエラー時の標準的なエラー情報レスポンスを表すDTOクラス。
 */
public class ApiErrorResponse {

    private String code;
    private String message;
    private List<FieldErrorResponse> fieldErrors = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FieldErrorResponse> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldErrorResponse> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public static ApiErrorResponse of(String code, String message) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
