package org.example.cloudfilestoragerestapi.controller;


import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.InvalidPathException;
import org.example.cloudfilestoragerestapi.security.UserDetailsImpl;
import org.example.cloudfilestoragerestapi.service.DirectoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/directory")
public class DirectoryController {

    private final DirectoryService directoryService;

    @GetMapping
    public ResponseEntity<List<ResourceResponseDto>> getDirectoryInfo(@RequestParam("path") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        if (!decodedPath.endsWith("/") && !decodedPath.isEmpty()) {
            throw new InvalidPathException("Invalid path");
        }

        List<ResourceResponseDto> resourceResponseDtos = directoryService.getDirectoryResources(decodedPath, userId);
        return ResponseEntity.ok(resourceResponseDtos);
    }

    @PostMapping
    public ResponseEntity<ResourceResponseDto> createNewDirectory(@RequestParam("path") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        if (!decodedPath.endsWith("/") && !decodedPath.isEmpty()) {
            throw new InvalidPathException("Invalid path");
        }

        ResourceResponseDto resourceResponseDto = directoryService.createDirectory(userId, decodedPath);
        return ResponseEntity.ok(resourceResponseDto);
    }
}
