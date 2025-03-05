package org.example.cloudfilestoragerestapi.service;


import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.example.cloudfilestoragerestapi.util.ItemUtil.getItemsFromResult;
import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.*;

@Service
@RequiredArgsConstructor
public class DirectoryService {


    private final MinioService minioService;



    @Value("${EMPTY_FILE}")
    private String emptyFile;


    public List<ResourceResponseDto> getDirectoryResources(String path, int userId) {
            List<Item> allFiles = minioService.getItemsFromMinio(userId, path);

            if (allFiles.isEmpty()) {
                throw new ResourceNotFoundException("Resource not found");
            }

            List<ResourceResponseDto> resourceResponseDtos = new ArrayList<>();

            for (Item item : allFiles) {

                if (getResourceNameWithoutPath(item.objectName()).equals(emptyFile)){
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
        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId,path);
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
                .path(path)
                .name(getResourceNameWithoutPath(path))
                .size(0).build();
    }


public InputStream getDirectoryAsStream(int userId, String path) {

    List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, path);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


    try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

        for (Item item : allFiles) {

            String fileInDir = getResourceNameWithoutPath(item.objectName());
            String pathInDir = getFilePathInDirectory(item.objectName(),path);

            if (fileInDir.equals(emptyFile)) {
                throw new ResourceNotFoundException("Nothing to download");
            }


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

    return new ByteArrayInputStream(byteArrayOutputStream .toByteArray());
}





}
