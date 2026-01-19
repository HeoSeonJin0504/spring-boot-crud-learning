package com.example.demo.service;

import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Long userIndex) {
        User user = userRepository.findById(userIndex)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return new UserResponseDto(user);
    }

    public UserResponseDto getUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
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

        User user = new User();
        user.setUserId(requestDto.getUserId());
        user.setPassword(requestDto.getPassword());
        user.setName(requestDto.getName());
        user.setGender(requestDto.getGender());
        user.setPhone(requestDto.getPhone());
        user.setEmail(requestDto.getEmail());

        User savedUser = userRepository.save(user);
        return new UserResponseDto(savedUser);
    }

    @Transactional
    public UserResponseDto updateUser(Long userIndex, UserRequestDto requestDto) {
        // 🆕 본인 인증 체크
        validateUserOwnership(userIndex);

        User user = userRepository.findById(userIndex)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        user.setName(requestDto.getName());
        user.setGender(requestDto.getGender());

        // 전화번호 변경 시 중복 체크
        if (!user.getPhone().equals(requestDto.getPhone())) {
            if (userRepository.existsByPhone(requestDto.getPhone())) {
                throw new RuntimeException("이미 존재하는 전화번호입니다");
            }
            user.setPhone(requestDto.getPhone());
        }

        // 이메일 변경 시 중복 체크 (이메일이 있을 경우만)
        if (requestDto.getEmail() != null && !requestDto.getEmail().isEmpty()) {
            if (!requestDto.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(requestDto.getEmail())) {
                    throw new RuntimeException("이미 존재하는 이메일입니다");
                }
                user.setEmail(requestDto.getEmail());
            }
        } else {
            user.setEmail(null);
        }

        return new UserResponseDto(user);
    }

    @Transactional
    public void deleteUser(Long userIndex) {
        // 🆕 본인 인증 체크
        validateUserOwnership(userIndex);

        if (!userRepository.existsById(userIndex)) {
            throw new RuntimeException("사용자를 찾을 수 없습니다");
        }
        userRepository.deleteById(userIndex);
    }

    // 본인 인증 검증 메서드
    private void validateUserOwnership(Long userIndex) {
        // 1. SecurityContext에서 현재 로그인한 userId 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = (String) authentication.getPrincipal();

        // 2. 수정/삭제 대상 사용자 조회
        User targetUser = userRepository.findById(userIndex)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 3. 본인이 아니면 예외 발생
        if (!targetUser.getUserId().equals(currentUserId)) {
            throw new RuntimeException("본인의 정보만 수정/삭제할 수 있습니다");
        }
    }
}