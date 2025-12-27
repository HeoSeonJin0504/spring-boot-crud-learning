package com.example.demo.controller;

import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // @Controller + @ResponseBody (JSON 자동 반환)
@RequestMapping("/api/users")  // 기본 경로
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users - 모든 사용자 조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // GET /api/users/{id} - 특정 사용자 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // POST /api/users - 사용자 생성
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto user = userService.createUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // PUT /api/users/{id} - 사용자 수정
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto user = userService.updateUser(id, requestDto);
        return ResponseEntity.ok(user);
    }

    // DELETE /api/users/{id} - 사용자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}