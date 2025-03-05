package org.example.cloudfilestoragerestapi.service;


import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.*;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioService minioService;



    public void deleteFile(int userId, String path) {
        if (path.contains("/")) {
            String pathToFile = getResourcePathWithoutName(path);
            minioService.putEmptyObject(getUserRootFolder(userId) + pathToFile);
        }
        minioService.removeObject(getUserRootFolder(userId) + path);

    }

    public ResourceResponseDto getFileInfo(int userId, String path) {
        StatObjectResponse objectStat = minioService.getObjectStat(getUserRootFolder(userId) + path);

        return ResourceResponseDto.builder()
                .name(getResourceNameWithoutPath(path))
                .size(objectStat.size())
                .path(path)
                .type("FILE")
                .build();
    }


    public InputStream getFileAsStream(int userId, String path) {
        return minioService.getObject(getUserRootFolder(userId) + path);
    }
}
