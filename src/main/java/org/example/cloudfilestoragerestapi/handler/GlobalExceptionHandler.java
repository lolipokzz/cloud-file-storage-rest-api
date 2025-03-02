package org.example.cloudfilestoragerestapi.handler;


import org.example.cloudfilestoragerestapi.dto.response.ErrorResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceNotFoundException;
import org.example.cloudfilestoragerestapi.exception.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().message(e.getMessage()).build();
        return ResponseEntity.status(409).body(errorResponseDto);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException() {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().message("Validation error").build();
        return ResponseEntity.status(400).body(errorResponseDto);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException() {
        return ResponseEntity.status(401).body(ErrorResponseDto.builder().message("Invalid username or password").build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(404).body(ErrorResponseDto.builder().message(e.getMessage()).build());
    }


/*    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().message("Unexpected error").build();
        return ResponseEntity.status(500).body(errorResponseDto);
    }*/
}
