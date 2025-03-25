package org.example.cloudfilestoragerestapi.service;


import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceAlreadyExists;
import org.example.cloudfilestoragerestapi.util.ResourceNamingUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final DirectoryService directoryService;

    private final FileService fileService;
    private final MinioService minioService;

    private final ResourceNamingUtil resourceNamingUtil;

    public ResourceResponseDto getResourceByPath(int userId, String path) {
        String resourceType = resourceNamingUtil.getResourceTypeByName(path);
        if (resourceType.equals("FILE")) {
            return fileService.getFileInfo(userId, path);
        } else {
            return directoryService.getDirectoryInfo(userId, path);
        }
    }


    public void deleteResource(int userId, String path) {
        String resourceType = resourceNamingUtil.getResourceTypeByName(path);
        if (resourceType.equals("FILE")) {
            fileService.delete(userId, path);
        } else {
            directoryService.delete(userId, path);
        }
    }


    public InputStream getResourceAsStream(int userId, String path) {
        String resourceType = resourceNamingUtil.getResourceTypeByName(path);
        if (resourceType.equals("FILE")) {
            return fileService.getFileAsStream(userId, path);
        } else {
            return directoryService.getDirectoryAsStream(userId, path);
        }
    }

    public ResourceResponseDto moveResource(int userId, String fromPath, String toPath) {
        String resourceType = resourceNamingUtil.getResourceTypeByName(toPath);
        if (resourceType.equals("FILE")) {
            return fileService.move(userId, fromPath, toPath);
        } else {
            return directoryService.move(userId, fromPath, toPath);
        }
    }

    public List<ResourceResponseDto> findResources(int userId, String query) {
        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, "");
        List<ResourceResponseDto> resources = new ArrayList<>();
        for (Item item : allFiles) {
            if (item.objectName().contains(query)) {
                resources.add(getResourceByPath(userId, resourceNamingUtil.getResourcePathWithoutRootFolder(item.objectName())));
            }
        }
        return resources;
    }


    public List<ResourceResponseDto> uploadResource(int userId, String path, List<MultipartFile> files) {
        List<ResourceResponseDto> resources = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = path + file.getOriginalFilename();

            List<Item> allFiles = minioService.getItemsFromMinio(userId, fileName);
            if (!allFiles.isEmpty()) {
                throw new ResourceAlreadyExists("Resource already exists");
            }

            minioService.putObject(fileName, userId, file);
            resources.add(fileService.getFileInfo(userId, fileName));
        }
        return resources;
    }
}





