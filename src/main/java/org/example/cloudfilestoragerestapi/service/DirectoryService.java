package org.example.cloudfilestoragerestapi.service;


import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.*;

@Service
@RequiredArgsConstructor
public class DirectoryService {


    private final MinioService minioService;



    public List<ResourceResponseDto> getDirectoryResources(String path, int userId) {
        List<Item> allFiles = minioService.getItemsFromMinio(userId, path);

        if (allFiles.isEmpty()) {
            throw new ResourceNotFoundException("Resource not found");
        }

        List<ResourceResponseDto> resourceResponseDtos = new ArrayList<>();

        for (Item item : allFiles) {

            if (item.objectName().equals(getUserRootFolder(userId)+path)) {
                continue;
            }

            if (item.isDir()) {
                resourceResponseDtos.add(ResourceResponseDto.builder()
                        .name(getResourceNameWithoutPath(item.objectName()))
                        .path(path)
                        .size(item.size())
                        .type("DIRECTORY")
                        .build());
            } else {
                resourceResponseDtos.add(ResourceResponseDto.builder()
                        .name(getResourceNameWithoutPath(item.objectName()))
                        .path(path)
                        .size(item.size())
                        .type("FILE")
                        .build());
            }
        }
        return resourceResponseDtos;
    }


    public void deleteDirectory(int userId, String path) {
        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, path);
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
                .path(getResourcePathWithoutName(path))
                .name(getResourceNameWithoutPath(path))
                .size(0).build();
    }


    public InputStream getDirectoryAsStream(int userId, String path) {

        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

            for (Item item : allFiles) {

                String pathInDir = getFilePathInDirectory(item.objectName(), path);


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


    //TODO почистить это говно
    public ResourceResponseDto moveDirectory(int userId, String fromPath, String toPath) {
        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, fromPath);
        for (Item item : allFiles) {
            String fileInDir = getFilePathInDirectory2(item.objectName(), fromPath);
            int index = 0;
            for (int i = 0; i < fileInDir.length() - 1; i++) {
                if (fileInDir.charAt(i) == '/') {
                    index += 1;
                }
                if (index == 2) {
                    fileInDir = fileInDir.substring(i + 1);
                    break;
                }
            }


            String targetPath = getUserRootFolder(userId) + toPath + fileInDir;
            minioService.copyObject(item.objectName(), targetPath);
            minioService.removeObject(item.objectName());
        }
        return ResourceResponseDto
                .builder()
                .type("DIRECTORY")
                .name(getResourceNameWithoutPath(toPath))
                .path(getResourcePathWithoutName(fromPath))
                .size(0)
                .build();
    }


    public ResourceResponseDto createDirectory(int userId, String path) {
        String fullPath = getUserRootFolder(userId) + path;
        minioService.putEmptyObject(fullPath);
        return ResourceResponseDto.builder()
                .path(getResourcePathWithoutName(path))
                .name(getResourceNameWithoutPath(path))
                .size(0)
                .type("DIRECTORY")
                .build();
    }


}
