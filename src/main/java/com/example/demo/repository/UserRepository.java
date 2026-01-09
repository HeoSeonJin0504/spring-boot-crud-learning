package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // userId로 찾기 (로그인용)
    Optional<User> findByUserId(String userId);

    // userId 존재 여부 확인
    boolean existsByUserId(String userId);

    // 이메일로 찾기 (선택 사항)
    Optional<User> findByEmail(String email);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 전화번호로 찾기
    Optional<User> findByPhone(String phone);

    // 전화번호 존재 여부 확인
    boolean existsByPhone(String phone);
}