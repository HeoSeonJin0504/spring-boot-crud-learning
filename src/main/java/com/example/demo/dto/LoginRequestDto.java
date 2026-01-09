package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "아이디는 필수입니다")
    private String userId;  // 이메일 → 아이디로 변경

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}