package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponseDto register(UserRequestDto requestDto) {
        // userId 중복 체크
        if (userRepository.existsByUserId(requestDto.getUserId())) {
            throw new RuntimeException("이미 존재하는 아이디입니다");
        }

        // 전화번호 중복 체크
        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new RuntimeException("이미 존재하는 전화번호입니다");
        }

        // 이메일 중복 체크 (이메일이 제공된 경우만)
        if (requestDto.getEmail() != null && !requestDto.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                throw new RuntimeException("이미 존재하는 이메일입니다");
            }
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = new User();
        user.setUserId(requestDto.getUserId());
        user.setPassword(encodedPassword);
        user.setName(requestDto.getName());
        user.setGender(requestDto.getGender());
        user.setPhone(requestDto.getPhone());
        user.setEmail(requestDto.getEmail());

        User savedUser = userRepository.save(user);
        return new UserResponseDto(savedUser);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        // userId로 사용자 찾기
        User user = userRepository.findByUserId(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다");
        }

        // 액세스 토큰 생성 (15분)
        String accessToken = jwtUtil.generateAccessToken(user.getUserId());

        // 리프레시 토큰 생성 (7일)
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        // ✅ 리프레시 토큰을 해시화하지 않고 그대로 저장
        // 기존 리프레시 토큰 삭제 (있다면)
        refreshTokenRepository.deleteByUserIndex(user.getUserIndex());

        // 새 리프레시 토큰 저장
        RefreshToken refreshTokenEntity = new RefreshToken(
                user.getUserIndex(),
                refreshToken,  // ✅ 해시화 제거! 그대로 저장
                LocalDateTime.now().plusDays(7)
        );
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponseDto(accessToken, refreshToken, user.getUserId(), user.getName());
    }

    // 🆕 리프레시 토큰으로 액세스 토큰 재발급
    @Transactional
    public TokenResponseDto refresh(RefreshRequestDto requestDto) {
        // 리프레시 토큰 유효성 검증
        if (!jwtUtil.validateToken(requestDto.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다");
        }

        // 리프레시 토큰에서 userId 추출
        String userId = jwtUtil.getUserIdFromToken(requestDto.getRefreshToken());

        // DB에서 사용자 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // DB에서 저장된 리프레시 토큰 조회
        RefreshToken storedToken = refreshTokenRepository.findByUserIndex(user.getUserIndex())
                .orElseThrow(() -> new RuntimeException("리프레시 토큰이 존재하지 않습니다"));

        // 만료 확인
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new RuntimeException("만료된 리프레시 토큰입니다");
        }

        // ✅ 리프레시 토큰 검증 (해시 비교 → 직접 비교)
        if (!requestDto.getRefreshToken().equals(storedToken.getToken())) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다");
        }

        // 새 액세스 토큰 발급
        String newAccessToken = jwtUtil.generateAccessToken(userId);

        return new TokenResponseDto(newAccessToken);
    }

    // 🆕 로그아웃 (리프레시 토큰 삭제)
    @Transactional
    public void logout(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        refreshTokenRepository.deleteByUserIndex(user.getUserIndex());
    }
}