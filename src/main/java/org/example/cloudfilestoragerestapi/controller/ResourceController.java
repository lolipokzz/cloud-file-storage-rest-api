package org.example.cloudfilestoragerestapi.controller;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.getResourceTypeByName;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;


    @GetMapping
    public ResponseEntity<ResourceResponseDto> getResourceInfo(@RequestParam(value = "path") String path, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        ResourceResponseDto resourceResponseDto = resourceService.getResourceByPath(userId, path);
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
    public ResponseEntity<List<ResourceResponseDto>> uploadResource(@RequestParam(value = "file") MultipartFile file, @RequestParam("path") String path) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getUser().getId();

        return null;
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

        return null;
    }


}
