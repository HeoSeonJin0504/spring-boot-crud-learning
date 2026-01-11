package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register - 회원가입
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto user = authService.register(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // POST /api/auth/login - 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto response = authService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    // 🆕 POST /api/auth/refresh - 액세스 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@Valid @RequestBody RefreshRequestDto requestDto) {
        TokenResponseDto response = authService.refresh(requestDto);
        return ResponseEntity.ok(response);
    }

    // 🆕 POST /api/auth/logout - 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String userId) {
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }
}