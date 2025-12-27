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

@Service  // 스프링이 관리하는 서비스 빈
@RequiredArgsConstructor  // final 필드 생성자 자동 생성 (의존성 주입)
@Transactional(readOnly = true)  // 기본적으로 읽기 전용 트랜잭션
public class UserService {

    private final UserRepository userRepository;

    // 모든 사용자 조회
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::new)  // Entity → DTO 변환
                .collect(Collectors.toList());
    }

    // ID로 사용자 조회
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return new UserResponseDto(user);
    }

    // 사용자 생성
    @Transactional  // 쓰기 작업은 별도 트랜잭션
    public UserResponseDto createUser(UserRequestDto requestDto) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다");
        }

        // DTO → Entity 변환
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());  // 실제로는 암호화 필요!
        user.setName(requestDto.getName());

        User savedUser = userRepository.save(user);
        return new UserResponseDto(savedUser);
    }

    // 사용자 수정
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        user.setName(requestDto.getName());
        // 이메일, 비밀번호는 별도 API로 변경하는 것이 일반적

        return new UserResponseDto(user);
    }

    // 사용자 삭제
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("사용자를 찾을 수 없습니다");
        }
        userRepository.deleteById(id);
    }
}