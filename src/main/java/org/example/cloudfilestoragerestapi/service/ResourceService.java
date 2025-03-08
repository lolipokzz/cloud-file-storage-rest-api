package org.example.cloudfilestoragerestapi.service;


import io.minio.MinioClient;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.getResourcePathWithoutRootFolder;
import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.getResourceTypeByName;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final DirectoryService directoryService;

    private final FileService fileService;
    private final MinioService minioService;

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
            return directoryService.getDirectoryAsStream(userId, path);
        }
    }

    public ResourceResponseDto moveResource(int userId, String fromPath, String toPath) {
        String resourceType = getResourceTypeByName(toPath);
        if (resourceType.equals("FILE")) {
            return fileService.moveFile(userId, fromPath, toPath);
        } else {
            return directoryService.moveDirectory(userId, fromPath, toPath);
        }
    }

    public List<ResourceResponseDto> getResourceList(int userId, String query) {
        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, "");
        List<ResourceResponseDto> resources = new ArrayList<>();
        for (Item item : allFiles) {
            String pathWithoutRootDirectory = getResourcePathWithoutRootFolder(item.objectName());
            String[] allResources = pathWithoutRootDirectory.split("/");
            for (int i = 0; i < allResources.length; i++) {
                if (allResources[i].contains(query)) {
                    if (i == allResources.length - 1) {
                        resources.add(fileService.getFileInfo(userId, pathWithoutRootDirectory));
                    }

                }
            }
        }
        return resources;
    }


    public List<ResourceResponseDto> uploadResource(int userId, String path, List<MultipartFile> files) {
        List<ResourceResponseDto> resources = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = path+file.getOriginalFilename();
            minioService.putObject(fileName,userId,file);
            resources.add(fileService.getFileInfo(userId, fileName));
        }
        return resources;
    }
}





