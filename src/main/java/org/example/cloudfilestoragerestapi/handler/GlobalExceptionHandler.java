package org.example.cloudfilestoragerestapi.handler;


import org.example.cloudfilestoragerestapi.dto.response.MessageResponseDto;
import org.example.cloudfilestoragerestapi.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<MessageResponseDto> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        MessageResponseDto messageResponseDto = MessageResponseDto.builder().message(e.getMessage()).build();
        return ResponseEntity.status(409).body(messageResponseDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        MessageResponseDto messageResponseDto = MessageResponseDto.builder().message(message).build();
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


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<MessageResponseDto> handleMaxUploadSizeExceededException() {
        return ResponseEntity.status(400).body(MessageResponseDto.builder().message("Resource is too big").build());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<MessageResponseDto> handleMethodValidationException() {
        return ResponseEntity.status(400).body(MessageResponseDto.builder().message("Incorrect path").build());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDto> handleException() {
        MessageResponseDto errorResponseDto = MessageResponseDto.builder().message("Unexpected error").build();
        return ResponseEntity.status(500).body(errorResponseDto);
    }
}
