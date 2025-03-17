package org.example.cloudfilestoragerestapi.handler;


import org.example.cloudfilestoragerestapi.dto.response.MessageResponseDto;
import org.example.cloudfilestoragerestapi.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<MessageResponseDto> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        MessageResponseDto messageResponseDto = MessageResponseDto.builder().message(e.getMessage()).build();
        return ResponseEntity.status(409).body(messageResponseDto);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponseDto> handleMethodArgumentNotValidException() {
        MessageResponseDto messageResponseDto = MessageResponseDto.builder().message("Validation error").build();
        return ResponseEntity.status(400).body(messageResponseDto);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<MessageResponseDto> handleAuthenticationException() {
        return ResponseEntity.status(401).body(MessageResponseDto.builder().message("Invalid username or password").build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponseDto> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(404).body(MessageResponseDto.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(InvalidPathException.class)
    public ResponseEntity<MessageResponseDto> handleInvalidPathException(InvalidPathException e) {
        return ResponseEntity.status(400).body(MessageResponseDto.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(ResourceDoesNotExistException.class)
    public ResponseEntity<MessageResponseDto> handleDirectoryDoesNotExistException(ResourceDoesNotExistException e) {
        return ResponseEntity.status(404).body(MessageResponseDto.builder().message(e.getMessage()).build());
    }


    @ExceptionHandler(ResourceAlreadyExists.class)
    public ResponseEntity<MessageResponseDto> handleDirectoryAlreadyExists(ResourceAlreadyExists e) {
        return ResponseEntity.status(409).body(MessageResponseDto.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(UploadException.class)
    public ResponseEntity<MessageResponseDto> handleUploadException(UploadException e) {
        return ResponseEntity.status(400).body(MessageResponseDto.builder().message(e.getMessage()).build());
    }


/*    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDto> handleException(Exception e) {
        MessageResponseDto errorResponseDto = MessageResponseDto.builder().message("Unexpected error").build();
        return ResponseEntity.status(500).body(errorResponseDto);
    }*/
}
