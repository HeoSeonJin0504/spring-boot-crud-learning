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

    // userIndex로 조회
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
}