package org.example.cloudfilestoragerestapi.service;


import io.minio.*;
import io.minio.messages.Item;
import io.minio.messages.RestoreRequest;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.example.cloudfilestoragerestapi.util.ItemUtil.getItemsFromResult;
import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.*;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final MinioClient minioClient;

    public ResourceResponseDto getResourceByPath(int userId, String path) {
        String resourceType = getResourceTypeByName(path);
        if (resourceType.equals("FILE")) {
            try {
                StatObjectResponse objectStat = minioClient.statObject(StatObjectArgs.builder()
                        .bucket("user-files")
                        .object("user-" + userId + "-files/" + path)
                        .build());
                return ResourceResponseDto.builder()
                        .name(getResourceNameWithoutPath(path))
                        .size(objectStat.size())
                        .path(path)
                        .type(resourceType)
                        .build();
            } catch (Exception e) {
                throw new ResourceNotFoundException("Resource not found");
            }
        } else {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket("user-files").prefix("user-" + userId + "-files/" + path).build()
            );
            List<Item> allFiles = getItemsFromResult(results);
            if (allFiles.isEmpty()) {
                throw new ResourceNotFoundException("Directory not found: " + path);
            }
            return ResourceResponseDto.builder()
                    .type(resourceType)
                    .path(path)
                    .name(getResourceNameWithoutPath(path))
                    .size(0).build();
        }
    }

    public void deleteResource(int userId, String path) {
        String resourceType = getResourceTypeByName(path);
        if (resourceType.equals("FILE")) {
            deleteFile(userId, path);
        } else {
            deleteDirectory(userId, path);
        }

    }

    private void deleteFile(int userId, String path) {
        try {
            if (path.contains("/")) {
                String pathToFile = getResourcePathWithoutName(path);
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket("user-files")
                                .object("user-" + userId + "-files/" + pathToFile + "empty-file")
                                .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                                .build());

            }
            minioClient.removeObject(RemoveObjectArgs.builder().bucket("user-files").object("user-" + userId + "-files/" + path).build());


        } catch (Exception e) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    private void deleteDirectory(int userId, String path) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket("user-files")
                        .prefix("user-" + userId + "-files/" + path)
                        .recursive(true)
                        .build());
        List<Item> allFiles = getItemsFromResult(results);
        for (Item item : allFiles) {
            try{
                minioClient.removeObject(RemoveObjectArgs.builder().bucket("user-files").object(item.objectName()).build());
            }catch (Exception e){
                throw new ResourceNotFoundException("Resource not found");
            }

        }


    }


    public List<ResourceResponseDto> uploadResource(MultipartFile file, String path, int userId) {
        return null;
    }


}
