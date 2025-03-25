package org.example.cloudfilestoragerestapi.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.DownloadException;
import org.example.cloudfilestoragerestapi.exception.UploadException;
import org.example.cloudfilestoragerestapi.security.UserDetailsImpl;
import org.example.cloudfilestoragerestapi.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;


    @GetMapping
    public ResponseEntity<ResourceResponseDto> getResourceInfo(@RequestParam(value = "path") @Pattern(regexp = "^(|([^\\\\/\\\\\\\\@#$%^&*()+=<>?\\\\[\\\\]{}|~;,:\\\"'].*))$") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        ResourceResponseDto resourceResponseDto = resourceService.getResourceByPath(userId, path);
        return ResponseEntity.status(200).body(resourceResponseDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteResource(@RequestParam(value = "path") @Pattern(regexp = "^(|([^\\\\/\\\\\\\\@#$%^&*()+=<>?\\\\[\\\\]{}|~;,:\\\"'].*))$") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        resourceService.deleteResource(userId, path);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<List<ResourceResponseDto>> uploadResource(@RequestPart(value = "object") List<MultipartFile> file, @RequestParam("path") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        if (file.isEmpty()) {
            throw new UploadException("File is empty");
        }

        List<ResourceResponseDto> resources = resourceService.uploadResource(userId, path, file);
        return ResponseEntity.status(201).body(resources);
    }

    @GetMapping("/download")
    public ResponseEntity<Void> downloadResource(@RequestParam("path") @Pattern(regexp = "^(|([^\\\\/\\\\\\\\@#$%^&*()+=<>?\\\\[\\\\]{}|~;,:\\\"'].*))$") String path, @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response) {
        int userId = userDetails.getUser().getId();

        try (InputStream inputStream = resourceService.getResourceAsStream(userId, path);
             OutputStream outputStream = response.getOutputStream()) {
            response.setContentType("application/octet-stream; charset=utf-8");
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new DownloadException("Something went wrong");
        }
        return ResponseEntity.status(200).build();
    }


    @GetMapping("/move")
    public ResponseEntity<ResourceResponseDto> moveResource(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("from") String from, @RequestParam("to") String to) {
        int userId = userDetails.getUser().getId();
        ResourceResponseDto resourceResponseDto = resourceService.moveResource(userId, from, to);
        return ResponseEntity.status(200).body(resourceResponseDto);
    }


    @GetMapping("/search")
    public ResponseEntity<List<ResourceResponseDto>> findResources(@RequestParam("query") String query, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        List<ResourceResponseDto> resources = resourceService.findResources(userId, query);
        return ResponseEntity.status(200).body(resources);
    }

}
