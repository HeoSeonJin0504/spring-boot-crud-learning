package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDto {

    private int status;           // HTTP 상태 코드
    private String error;         // 에러 타입
    private String message;       // 에러 메시지
    private LocalDateTime timestamp;  // 발생 시간

    public ErrorResponseDto(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}