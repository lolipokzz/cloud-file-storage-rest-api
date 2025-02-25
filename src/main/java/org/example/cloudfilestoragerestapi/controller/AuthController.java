package org.example.cloudfilestoragerestapi.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.request.NewUserRequestDto;
import org.example.cloudfilestoragerestapi.dto.request.UserLoginRequestDto;
import org.example.cloudfilestoragerestapi.dto.response.ErrorResponseDto;
import org.example.cloudfilestoragerestapi.dto.response.UserResponseDto;
import org.example.cloudfilestoragerestapi.security.UserDetailsImpl;
import org.example.cloudfilestoragerestapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid NewUserRequestDto newUserRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(400).body(ErrorResponseDto.builder().message(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()).build());
        }

        UserResponseDto userResponseDto = authService.saveNewUser(newUserRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }


    @PostMapping("/sign-in")
    public ResponseEntity<UserResponseDto> signIn(@RequestBody UserLoginRequestDto userLoginRequestDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequestDto.getUsername(), userLoginRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails =(UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponseDto userResponseDto = UserResponseDto.builder().username(userDetails.getUsername()).build();
        return ResponseEntity.ok(userResponseDto);
    }



}
