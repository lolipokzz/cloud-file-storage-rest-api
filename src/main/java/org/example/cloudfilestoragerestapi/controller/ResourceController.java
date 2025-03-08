package org.example.cloudfilestoragerestapi.controller;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.security.UserDetailsImpl;
import org.example.cloudfilestoragerestapi.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;


    @GetMapping
    public ResponseEntity<ResourceResponseDto> getResourceInfo(@RequestParam(value = "path") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        ResourceResponseDto resourceResponseDto = resourceService.getResourceByPath(userId, decodedPath);
        return ResponseEntity.status(201).body(resourceResponseDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteResource(@RequestParam(value = "path") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        resourceService.deleteResource(userId, decodedPath);
        return ResponseEntity.status(204).build();
    }

    @PostMapping
    public ResponseEntity<List<ResourceResponseDto>> uploadResource(@RequestPart(value = "object") List<MultipartFile> file, @RequestParam("path") String path,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        List<ResourceResponseDto> resourceResponseDtos = resourceService.uploadResource(userId,decodedPath, file);
        return ResponseEntity.status(201).body(resourceResponseDtos);
    }

    @GetMapping("/download")
    public ResponseEntity<Void> downloadResource(@RequestParam("path") String path, @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response) {
        int userId = userDetails.getUser().getId();
        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        try (InputStream inputStream = resourceService.getResourceAsStream(userId, decodedPath);
             OutputStream outputStream = response.getOutputStream()) {
            response.setContentType("application/octet-stream; charset=utf-8");
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(200).build();
    }


    @GetMapping("/move")
    public ResponseEntity<ResourceResponseDto> moveResource(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("from") String from, @RequestParam("to") String to) {
        String decodedFrom = URLDecoder.decode(from, StandardCharsets.UTF_8);
        String decodedTo = URLDecoder.decode(to, StandardCharsets.UTF_8);
        int userId = userDetails.getUser().getId();
        ResourceResponseDto resourceResponseDto = resourceService.moveResource(userId, decodedFrom, decodedTo);
        return ResponseEntity.status(200).body(resourceResponseDto);
    }


    @GetMapping("/search")
    public ResponseEntity<List<ResourceResponseDto>> findResources(@RequestParam("query") String query, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8);
        int userId = userDetails.getUser().getId();
        List<ResourceResponseDto> resourceResponseDtos = resourceService.getResourceList(userId, decodedQuery);
        return ResponseEntity.status(200).body(resourceResponseDtos);
    }

}
