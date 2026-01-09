package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor  // 모든 필드를 받는 생성자 자동 생성
public class LoginResponseDto {

    private String token;  // JWT 토큰
    private String email;
    private String name;
}