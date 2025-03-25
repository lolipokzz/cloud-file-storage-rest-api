package org.example.cloudfilestoragerestapi.service;


import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceAlreadyExists;
import org.example.cloudfilestoragerestapi.exception.ResourceDoesNotExistException;
import org.example.cloudfilestoragerestapi.exception.ResourceNotFoundException;
import org.example.cloudfilestoragerestapi.util.ResourceNamingUtil;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class DirectoryService {


    private final MinioService minioService;

    private final ResourceNamingUtil resourceNamingUtil;


    public List<ResourceResponseDto> getDirectoryResources(String path, int userId) {
        List<Item> allFiles = minioService.getItemsFromMinio(userId, path);

        if (allFiles.isEmpty()) {
            throw new ResourceDoesNotExistException("Directory not found");
        }

        List<ResourceResponseDto> resourceResponseDtos = new ArrayList<>();

        for (Item item : allFiles) {

            if (item.objectName().equals(resourceNamingUtil.getUserRootFolder(userId) + path)) {
                continue;
            }

            String nameWithoutPath = resourceNamingUtil.getFileNameWithoutPath(item.objectName());

            resourceResponseDtos.add(ResourceResponseDto.builder()
                    .name(nameWithoutPath)
                    .path(path)
                    .size(item.size())
                    .type(item.isDir() ? "DIRECTORY" : "FILE")
                    .build());
        }
        return resourceResponseDtos;
    }


    public void delete(int userId, String path) {
        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, path);

        if (allFiles.isEmpty()) {
            throw new ResourceNotFoundException("Directory not found: " + path);
        }

        for (Item item : allFiles) {
            minioService.removeObject(item.objectName());

        }
    }


    public ResourceResponseDto getDirectoryInfo(int userId, String path) {
        List<Item> allFiles = minioService.getItemsFromMinio(userId, path);

        if (allFiles.isEmpty()) {
            throw new ResourceNotFoundException("Directory not found: " + path);
        }


        return ResourceResponseDto.builder()
                .type("DIRECTORY")
                .path(resourceNamingUtil.getDirectoryPathWithoutName(path))
                .name(resourceNamingUtil.getDirectoryNameWithoutPath(path))
                .size(0).build();
    }


    public InputStream getDirectoryAsStream(int userId, String path) {

        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

            for (Item item : allFiles) {

                String pathInDir = resourceNamingUtil.getFilePathInDirectory(item.objectName(), path, true);


                try (InputStream inputStream = minioService.getObject(item.objectName())) {

                    ZipEntry zipEntry = new ZipEntry(pathInDir);
                    zipOutputStream.putNextEntry(zipEntry);
                    IOUtils.copy(inputStream, zipOutputStream);
                    zipOutputStream.closeEntry();

                } catch (Exception e) {
                    throw new RuntimeException("Failed to process object: " + item.objectName(), e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to create ZIP archive", e);
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }


    public ResourceResponseDto move(int userId, String fromPath, String toPath) {

        String toPathWithoutName = resourceNamingUtil.getDirectoryPathWithoutName(toPath);
        String fromPathWithoutName = resourceNamingUtil.getDirectoryPathWithoutName(fromPath);
        String directoryName;

        if (!Objects.equals(fromPathWithoutName, toPathWithoutName)) {
            directoryName = resourceNamingUtil.getDirectoryNameWithoutPath(fromPath);
        } else {
            directoryName = resourceNamingUtil.getDirectoryNameWithoutPath(toPath);
        }

        List<ResourceResponseDto> directoryResources = getDirectoryResources(toPathWithoutName, userId);
        for (ResourceResponseDto resource : directoryResources) {
            if (resource.getName().equals(directoryName)) {
                throw new ResourceAlreadyExists("Directory already exists");
            }
        }


        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, fromPath);
        for (Item item : allFiles) {
            String fileInDir = resourceNamingUtil.getFilePathInDirectory(item.objectName(), fromPath, false);
            String targetPath = resourceNamingUtil.getUserRootFolder(userId) + toPath + fileInDir;
            minioService.copyObject(item.objectName(), targetPath);
            minioService.removeObject(item.objectName());
        }
        return ResourceResponseDto
                .builder()
                .type("DIRECTORY")
                .name(resourceNamingUtil.getFileNameWithoutPath(toPath))
                .path(resourceNamingUtil.getFilePathWithoutName(fromPath))
                .size(0)
                .build();
    }


    public ResourceResponseDto create(int userId, String path) {

        if (!resourceNamingUtil.isRootDirectory(path)) {
            List<Item> allFiles = minioService.getItemsFromMinio(userId, resourceNamingUtil.getDirectoryPathWithoutName(path));
            if (allFiles.isEmpty()) {
                throw new ResourceNotFoundException("Directory not found: " + resourceNamingUtil.getDirectoryPathWithoutName(path));
            }
        }

        List<Item> allFiles = minioService.getItemsFromMinio(userId, path);
        if (!allFiles.isEmpty()) {
            throw new ResourceAlreadyExists("Directory already exists");
        }


        String fullPath = resourceNamingUtil.getUserRootFolder(userId) + path;
        minioService.putEmptyObject(fullPath);
        return ResourceResponseDto.builder()
                .path(resourceNamingUtil.getDirectoryPathWithoutName(path))
                .name(resourceNamingUtil.getDirectoryNameWithoutPath(path))
                .size(0)
                .type("DIRECTORY")
                .build();
    }


}
