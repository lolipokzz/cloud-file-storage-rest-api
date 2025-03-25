package org.example.cloudfilestoragerestapi.controller;


import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.InvalidPathException;
import org.example.cloudfilestoragerestapi.security.UserDetailsImpl;
import org.example.cloudfilestoragerestapi.service.DirectoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/directory")
public class DirectoryController {

    private final DirectoryService directoryService;

    @GetMapping
    public ResponseEntity<List<ResourceResponseDto>> getDirectoryInfo(@RequestParam("path") @Pattern(regexp = "^(|([^\\\\/\\\\\\\\@#$%^&*()+=<>?\\\\[\\\\]{}|~;,:\\\"'].*))$") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();


        if (!path.endsWith("/") && !path.isEmpty()) {
            throw new InvalidPathException("Invalid path");
        }

        List<ResourceResponseDto> resources = directoryService.getDirectoryResources(path, userId);
        return ResponseEntity.ok(resources);
    }

    @PostMapping
    public ResponseEntity<ResourceResponseDto> createNewDirectory(@RequestParam("path") @Pattern(regexp = "^(|([^\\\\/\\\\\\\\@#$%^&*()+=<>?\\\\[\\\\]{}|~;,:\\\"'].*))$") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();

        if (!path.endsWith("/") && !path.isEmpty()) {
            throw new InvalidPathException("Invalid path");
        }

        ResourceResponseDto resources = directoryService.create(userId, path);
        return ResponseEntity.ok(resources);
    }
}
