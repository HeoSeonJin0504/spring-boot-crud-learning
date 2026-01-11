package com.example.demo.repository;

import com.example.demo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // userIndex로 리프레시 토큰 찾기
    Optional<RefreshToken> findByUserIndex(Long userIndex);

    // userIndex로 리프레시 토큰 삭제 (로그아웃 시 사용)
    void deleteByUserIndex(Long userIndex);

    // 만료된 토큰 삭제 (배치 작업용)
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}