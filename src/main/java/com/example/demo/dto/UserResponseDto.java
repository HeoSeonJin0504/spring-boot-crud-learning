package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDto {

    private Long id;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity → DTO 변환
    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.gender = user.getGender();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        // 비밀번호는 응답에서 제외!
    }
}