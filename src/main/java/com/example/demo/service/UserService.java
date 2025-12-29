package com.example.demo.service;

import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다");
        }

        // 🆕 전화번호 중복 체크
        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new RuntimeException("이미 존재하는 전화번호입니다");
        }

        // DTO → Entity 변환
        User user = new User();
        user.setName(requestDto.getName());
        user.setPassword(requestDto.getPassword());
        user.setGender(requestDto.getGender());
        user.setPhone(requestDto.getPhone());
        user.setEmail(requestDto.getEmail());

        User savedUser = userRepository.save(user);
        return new UserResponseDto(savedUser);
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 수정 가능한 필드만 업데이트
        user.setName(requestDto.getName());
        user.setGender(requestDto.getGender());

        // 이메일 변경 시 중복 체크
        if (!user.getEmail().equals(requestDto.getEmail())) {
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                throw new RuntimeException("이미 존재하는 이메일입니다");
            }
            user.setEmail(requestDto.getEmail());
        }

        // 🆕 전화번호 변경 시 중복 체크
        if (!user.getPhone().equals(requestDto.getPhone())) {
            if (userRepository.existsByPhone(requestDto.getPhone())) {
                throw new RuntimeException("이미 존재하는 전화번호입니다");
            }
            user.setPhone(requestDto.getPhone());
        }

        return new UserResponseDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("사용자를 찾을 수 없습니다");
        }
        userRepository.deleteById(id);
    }
}