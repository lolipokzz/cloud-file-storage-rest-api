package org.example.cloudfilestoragerestapi.exception;


import jakarta.validation.ConstraintViolationException;
import org.example.cloudfilestoragerestapi.dto.response.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().message(e.getMessage()).build();
        return ResponseEntity.status(409).body(errorResponseDto);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(ConstraintViolationException e) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().message(e.getMessage()).build();
        return ResponseEntity.status(400).body(errorResponseDto);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(401).body(ErrorResponseDto.builder().message("Invalid username or password").build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().message("Unexpected error").build();
        return ResponseEntity.status(500).body(errorResponseDto);
    }
}
