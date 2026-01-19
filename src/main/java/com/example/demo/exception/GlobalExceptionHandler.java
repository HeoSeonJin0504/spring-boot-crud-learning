package com.example.demo.exception;

import com.example.demo.dto.ErrorResponseDto;
import com.example.demo.dto.ValidationErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice  // 전역 예외 처리
public class GlobalExceptionHandler {

    // 400 Bad Request - Validation 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex) {

        // 모든 필드 에러 수집
        List<ValidationErrorResponseDto.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorResponseDto.FieldError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ValidationErrorResponseDto response = new ValidationErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "입력값 검증에 실패했습니다",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 400 Bad Request - 중복 리소스
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateResourceException(
            DuplicateResourceException ex) {

        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 401 Unauthorized - 인증 실패
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorizedException(
            UnauthorizedException ex) {

        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 403 Forbidden - 권한 없음
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> handleForbiddenException(
            ForbiddenException ex) {

        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 404 Not Found - 리소스 없음
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 500 Internal Server Error - 기타 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {

        ErrorResponseDto response = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "서버 내부 오류가 발생했습니다"
        );

        // 개발 환경에서는 실제 에러 메시지 출력 (디버깅용)
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}