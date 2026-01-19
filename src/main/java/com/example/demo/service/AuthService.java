package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        if (userRepository.existsByUserId(requestDto.getUserId())) {
            throw new DuplicateResourceException("이미 존재하는 아이디입니다");
        }

        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new DuplicateResourceException("이미 존재하는 전화번호입니다");
        }

        if (requestDto.getEmail() != null && !requestDto.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                throw new DuplicateResourceException("이미 존재하는 이메일입니다");
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
        User user = userRepository.findByUserId(requestDto.getUserId())
                .orElseThrow(() -> new UnauthorizedException("아이디 또는 비밀번호가 일치하지 않습니다"));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("아이디 또는 비밀번호가 일치하지 않습니다");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        refreshTokenRepository.deleteByUserIndex(user.getUserIndex());

        RefreshToken refreshTokenEntity = new RefreshToken(
                user.getUserIndex(),
                refreshToken,
                LocalDateTime.now().plusDays(7)
        );
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponseDto(accessToken, refreshToken, user.getUserId(), user.getName());
    }

    @Transactional
    public TokenResponseDto refresh(RefreshRequestDto requestDto) {
        if (!jwtUtil.validateToken(requestDto.getRefreshToken())) {
            throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다");
        }

        String userId = jwtUtil.getUserIdFromToken(requestDto.getRefreshToken());

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));

        RefreshToken storedToken = refreshTokenRepository.findByUserIndex(user.getUserIndex())
                .orElseThrow(() -> new UnauthorizedException("리프레시 토큰이 존재하지 않습니다"));

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new UnauthorizedException("만료된 리프레시 토큰입니다");
        }

        if (!requestDto.getRefreshToken().equals(storedToken.getToken())) {
            throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다");
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId);

        return new TokenResponseDto(newAccessToken);
    }

    @Transactional
    public void logout(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = (String) authentication.getPrincipal();

        if (!currentUserId.equals(userId)) {
            throw new ForbiddenException("본인만 로그아웃할 수 있습니다");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다"));

        refreshTokenRepository.deleteByUserIndex(user.getUserIndex());
    }
}