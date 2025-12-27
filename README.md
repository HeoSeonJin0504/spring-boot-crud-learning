# Spring Boot User CRUD API

## 🚀 기술 스택
- Java 17
- Spring Boot 3.4.1
- Spring Data JPA
- Spring Security
- MySQL 8.0
- Gradle
- Lombok

## 📦 주요 기능
- 사용자 CRUD (생성, 조회, 수정, 삭제)
- JPA Auditing (자동 생성일/수정일 관리)

## ⚙️ 로컬 실행 방법

1. MySQL 데이터베이스 생성
```sql
CREATE DATABASE testdb;
```

2. `application-local.yml` 생성
```yaml
spring:
  datasource:
    password: your-password
```

3. 실행
```bash
./gradlew bootRun
```

## 📌 API 엔드포인트
- `GET /api/users` - 전체 사용자 조회
- `GET /api/users/{id}` - 특정 사용자 조회
- `POST /api/users` - 사용자 생성
- `PUT /api/users/{id}` - 사용자 수정
- `DELETE /api/users/{id}` - 사용자 삭제

## 🔜 향후 계획
- [ ] 비밀번호 암호화 (BCrypt)
- [ ] JWT 인증 구현
- [ ] 예외 처리 개선