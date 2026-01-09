package com.example.demo.service;

import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
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

    public LoginResponseDto login(LoginRequestDto requestDto) {
        // userId로 사용자 찾기
        User user = userRepository.findByUserId(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다");
        }

        // JWT 토큰 생성 (userId 기반)
        String token = jwtUtil.generateToken(user.getUserId());

        return new LoginResponseDto(token, user.getUserId(), user.getName());
    }
}