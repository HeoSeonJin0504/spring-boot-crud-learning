package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ValidationErrorResponseDto {

    private int status;
    private String error;
    private String message;
    private List<FieldError> errors;  // 필드별 에러 목록
    private LocalDateTime timestamp;

    public ValidationErrorResponseDto(int status, String error, String message, List<FieldError> errors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    @Getter
    @Setter
    public static class FieldError {
        private String field;      // 필드명
        private String message;    // 에러 메시지

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}