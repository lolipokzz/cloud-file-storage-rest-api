package org.example.cloudfilestoragerestapi.controller;


import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.request.ResourceUploadRequestDto;
import org.example.cloudfilestoragerestapi.dto.response.MessageResponseDto;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.security.UserDetailsImpl;
import org.example.cloudfilestoragerestapi.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;


    @GetMapping
    public ResponseEntity<ResourceResponseDto> getResourceInfo(@RequestParam (value = "path") String path,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId= userDetails.getUser().getId();
        ResourceResponseDto resourceResponseDto = resourceService.getResourceByPath(userId, path);
        return ResponseEntity.status(201).body(resourceResponseDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteResource(@RequestParam (value = "path") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId= userDetails.getUser().getId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        resourceService.deleteResource(userId, decodedPath);
        return ResponseEntity.status(204).build();
    }

    @PostMapping
    public ResponseEntity<List<ResourceResponseDto>> uploadResource(@RequestParam (value = "file") MultipartFile file,@RequestParam("path") String path) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId= userDetails.getUser().getId();

        return null;
    }


}
