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
- 회원가입/로그인 (아이디+비밀번호 방식)
- JWT 인증 (액세스 토큰 15분 + 리프레시 토큰 7일)
- 비밀번호 암호화 (BCrypt)
- 리프레시 토큰 DB 저장 및 액세스 토큰 재발급
- 로그아웃 (리프레시 토큰 삭제)
- JWT 인증 필터 (Authorization 헤더 검증)
- 본인 인증 체크 (본인 정보만 수정/삭제 가능)  
- 전역 예외 처리 (통일된 에러 응답) 
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

### 인증 API (공개 - 토큰 불필요)
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/login` - 로그인 (액세스/리프레시 토큰 발급)
- `POST /api/auth/refresh` - 액세스 토큰 재발급
- 
### 인증 API (토큰 필요)
- `POST /api/auth/logout` - 로그아웃 (리프레시 토큰 삭제)

### 사용자 API (인증 필요 - Authorization 헤더 필수)
- `GET /api/users` - 전체 사용자 조회
- `GET /api/users/{userIndex}` - 특정 사용자 조회
- `GET /api/users/me` - 현재 로그인한 사용자 정보 조회
- `POST /api/users` - 사용자 생성
- `PUT /api/users/{userIndex}` - 사용자 수정 (본인만 가능) 
- `DELETE /api/users/{userIndex}` - 사용자 삭제 (본인만 가능) 

### API 인증 방법
인증이 필요한 API 호출 시 헤더에 액세스 토큰 포함:
```
Authorization: Bearer {accessToken}
```

## 🔒 보안 기능

### JWT 토큰 관리
- **액세스 토큰**: 15분 유효 (수정 가능)
- **리프레시 토큰**: 7일 유효 (수정 가능)
- 로그아웃 시 리프레시 토큰 삭제

### 본인 인증
- 사용자 수정/삭제 시 JWT 토큰의 userId와 대상 비교
- 본인이 아닐 경우 403 Forbidden 에러

### 비밀번호 보안
- BCrypt 해시 알고리즘 사용

### 이메일 중복 방지
- 빈 문자열 자동 NULL 변환

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