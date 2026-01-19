package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_index")
    private Long userIndex;

    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(nullable = true, unique = true, length = 100)
    private String email;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 저장 전에 빈 문자열을 NULL로 변환
    @PrePersist
    @PreUpdate
    private void convertEmptyStringToNull() {
        if (email != null && email.trim().isEmpty()) {
            email = null;
        }
    }
}