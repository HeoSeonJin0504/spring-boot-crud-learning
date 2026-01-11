# Spring Boot User CRUD API

## 🚀 기술 스택
- Java 17
- Spring Boot 4.0.1
- Spring Data JPA
- Spring Security + JWT
- MySQL 8.0
- Gradle
- Lombok
- BCrypt

## 📦 주요 기능
- 사용자 CRUD (생성, 조회, 수정, 삭제)
- 회원가입/로그인 (JWT 인증)
- 비밀번호 암호화 (BCrypt)
- JPA Auditing (자동 생성일/수정일 관리)

## ⚙️ 로컬 실행 방법

1. MySQL 데이터베이스 생성
```sql
CREATE DATABASE testdb;
```

2. 환경 설정 파일 생성
- 프로젝트 루트의 src/main/resources 경로에 application-local.yml 파일을 생성하고 아래 내용을 입력하세요.
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/testdb?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: MySQL비밀번호를입력  # 실제 MySQL 비밀번호 입력

jwt:
  secret: 64자이상의비밀키를입력  # 64자 이상의 비밀 키
  access-expiration: 900000      # 15분 (밀리초)
  refresh-expiration: 604800000  # 7일 (밀리초)
```
⚠️ 보안 주의: application-local.yml 파일은 Git에 커밋되지 않습니다. 로컬에서 직접 생성해야 합니다

3. 실행
- IntelliJ IDEA: DemoApplication.java 실행 또는 Shift + F10
- Gradle:
```bash
./gradlew bootRun
```

## 📌 API 엔드포인트

### 인증 API
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/login` - 로그인 (액세스/리프레시 토큰 발급)
- `POST /api/auth/refresh` - 액세스 토큰 재발급
- `POST /api/auth/logout` - 로그아웃 (리프레시 토큰 삭제)

### 사용자 API
- `GET /api/users` - 전체 사용자 조회
- `GET /api/users/{userIndex}` - 특정 사용자 조회
- `POST /api/users` - 사용자 생성
- `PUT /api/users/{userIndex}` - 사용자 수정
- `DELETE /api/users/{userIndex}` - 사용자 삭제

## 데이터베이스 스키마

### users 테이블
- user_index (BIGINT, PK, AUTO_INCREMENT) - 시스템 내부 고유 번호
- user_id (VARCHAR(50), UNIQUE, NOT NULL) - 로그인 아이디
- password (VARCHAR(100), NOT NULL) - 암호화된 비밀번호
- name (VARCHAR(50), NOT NULL) - 이름
- gender (VARCHAR(10), NOT NULL) - 성별
- phone (VARCHAR(20), UNIQUE, NOT NULL) - 전화번호
- email (VARCHAR(100), UNIQUE, NULL) - 이메일 (선택사항)
- created_at (DATETIME, NOT NULL) - 생성일
- updated_at (DATETIME, NOT NULL) - 수정일

### refresh_tokens 테이블
- id (BIGINT, PK, AUTO_INCREMENT)
- user_index (BIGINT, NOT NULL) - users 테이블 참조
- token (VARCHAR(500), NOT NULL) - 리프레시 토큰
- expires_at (DATETIME, NOT NULL) - 만료일
- created_at (DATETIME, NOT NULL) - 생성일