package com.example.quote.dto.response;

/**
 * フィールド単位の単体バリデーションエラー詳細を保持するレスポンスDTOクラス。
 */
public class FieldErrorResponse {

    private String field;
    private String message;

    public FieldErrorResponse() {
    }

    public FieldErrorResponse(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
