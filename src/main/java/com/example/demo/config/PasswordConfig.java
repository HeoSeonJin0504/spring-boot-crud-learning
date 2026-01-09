package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean  // Spring이 관리하는 빈으로 등록
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // bcrypt 알고리즘 사용
    }
}