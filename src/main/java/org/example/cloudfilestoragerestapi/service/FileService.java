package org.example.cloudfilestoragerestapi.service;


import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceAlreadyExists;
import org.example.cloudfilestoragerestapi.util.ResourceNamingUtil;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioService minioService;

    private final ResourceNamingUtil resourceNamingUtil;


    public void deleteFile(int userId, String path) {
        if (path.contains("/")) {
            String pathToFile = resourceNamingUtil.getFilePathWithoutName(path);
            minioService.putEmptyObject(resourceNamingUtil.getUserRootFolder(userId) + pathToFile);
        }
        minioService.removeObject(resourceNamingUtil.getUserRootFolder(userId) + path);

    }

    public ResourceResponseDto getFileInfo(int userId, String path) {
        long objectSize = minioService.getObjectSize(resourceNamingUtil.getUserRootFolder(userId) + path);
        return ResourceResponseDto.builder()
                .name(path.contains("/") ? resourceNamingUtil.getFileNameWithoutPath(path) : path)
                .size(objectSize)
                .path(path.contains("/") ? resourceNamingUtil.getFilePathWithoutName(path) : "")
                .type("FILE")
                .build();
    }


    public InputStream getFileAsStream(int userId, String path) {
        return minioService.getObject(resourceNamingUtil.getUserRootFolder(userId) + path);
    }


    public ResourceResponseDto moveFile(int userId, String fromPath, String toPath) {
        List<Item> items = minioService.getItemsFromMinio(userId, toPath);
        for (Item item : items) {
            if (resourceNamingUtil.getFileNameWithoutPath(item.objectName()).equals(resourceNamingUtil.getFileNameWithoutPath(toPath))) {
                throw new ResourceAlreadyExists("Resource with this name already exists in directory");
            }
        }

        String fullFromPath = resourceNamingUtil.getUserRootFolder(userId) + fromPath;
        String fullToPath = resourceNamingUtil.getUserRootFolder(userId) + toPath;

        minioService.copyObject(fullFromPath, fullToPath);
        deleteFile(userId, fromPath);
        long objectSize = minioService.getObjectSize(resourceNamingUtil.getUserRootFolder(userId) + toPath);
        return ResourceResponseDto.builder()
                .path(toPath)
                .name(resourceNamingUtil.getFileNameWithoutPath(fromPath))
                .type("FILE")
                .size(objectSize)
                .build();
    }


}
