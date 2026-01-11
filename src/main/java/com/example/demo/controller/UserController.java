package com.example.demo.controller;

import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userIndex}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userIndex) {
        UserResponseDto user = userService.getUserById(userIndex);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto user = userService.createUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{userIndex}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long userIndex,
            @Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto user = userService.updateUser(userIndex, requestDto);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userIndex}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userIndex) {
        userService.deleteUser(userIndex);
        return ResponseEntity.noContent().build();
    }

    // 🆕 현재 로그인한 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        // SecurityContext에서 현재 인증된 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();

        UserResponseDto user = userService.getUserByUserId(userId);
        return ResponseEntity.ok(user);
    }
}