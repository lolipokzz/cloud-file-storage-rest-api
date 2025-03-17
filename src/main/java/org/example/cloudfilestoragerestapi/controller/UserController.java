package org.example.cloudfilestoragerestapi.controller;


import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.UserResponseDto;
import org.example.cloudfilestoragerestapi.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/me")
@RequiredArgsConstructor
public class UserController {

    @GetMapping
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponseDto userResponseDto = UserResponseDto.builder().username(userDetails.getUsername()).build();
        return ResponseEntity.ok(userResponseDto);
    }
}
