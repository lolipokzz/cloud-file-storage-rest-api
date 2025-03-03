package org.example.cloudfilestoragerestapi.service;


import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.getResourceTypeByName;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final MinioClient minioClient;

    private final DirectoryService directoryService;

    private final FileService fileService;

    public ResourceResponseDto getResourceByPath(int userId, String path) {
        String resourceType = getResourceTypeByName(path);
        if (resourceType.equals("FILE")) {
            return fileService.getFileInfo(userId, path);
        } else {
            return directoryService.getDirectoryInfo(userId, path);
        }
    }


    public void deleteResource(int userId, String path) {
        String resourceType = getResourceTypeByName(path);
        if (resourceType.equals("FILE")) {
            fileService.deleteFile(userId, path);
        } else {
            directoryService.deleteDirectory(userId, path);
        }
    }


    public InputStream getResourceAsStream(int userId, String path) {
        String resourceType = getResourceTypeByName(path);
        if (resourceType.equals("FILE")) {
            return fileService.getFileAsStream(userId, path);
        } else {
            return directoryService.getDirectoryAsStream(userId,path);
        }

    }

}
