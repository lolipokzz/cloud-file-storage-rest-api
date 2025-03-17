package org.example.cloudfilestoragerestapi.service;


import io.minio.StatObjectResponse;
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
            String pathToFile =resourceNamingUtil.getFilePathWithoutName(path);
            minioService.putEmptyObject(resourceNamingUtil.getUserRootFolder(userId) + pathToFile);
        }
        minioService.removeObject(resourceNamingUtil.getUserRootFolder(userId) + path);

    }

    public ResourceResponseDto getFileInfo(int userId, String path) {
        StatObjectResponse objectStat = minioService.getObjectStat(resourceNamingUtil.getUserRootFolder(userId) + path);

        if (path.contains("/")) {
            return ResourceResponseDto.builder()
                    .name(resourceNamingUtil.getFileNameWithoutPath(path))
                    .size(objectStat.size())
                    .path(resourceNamingUtil.getFilePathWithoutName(path))
                    .type("FILE")
                    .build();

        }else {
            return ResourceResponseDto.builder()
                    .name(path)
                    .size(objectStat.size())
                    .path("")
                    .type("FILE")
                    .build();
        }
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


        minioService.copyObject(resourceNamingUtil.getUserRootFolder(userId) + fromPath, resourceNamingUtil.getUserRootFolder(userId) + toPath);
        deleteFile(userId, fromPath);
        StatObjectResponse objectStat = minioService.getObjectStat(resourceNamingUtil.getUserRootFolder(userId) + toPath);
        return ResourceResponseDto.builder()
                .path(toPath)
                .name(resourceNamingUtil.getFileNameWithoutPath(fromPath))
                .type("FILE")
                .size(objectStat.size())
                .build();
    }


}
