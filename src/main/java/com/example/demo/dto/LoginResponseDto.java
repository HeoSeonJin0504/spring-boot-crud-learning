package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;   // 액세스 토큰 (15분)
    private String refreshToken;  // 리프레시 토큰 (7일)
    private String userId;
    private String name;
}