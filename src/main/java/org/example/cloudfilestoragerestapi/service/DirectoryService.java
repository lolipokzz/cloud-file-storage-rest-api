package org.example.cloudfilestoragerestapi.service;


import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.example.cloudfilestoragerestapi.util.ItemUtil.getItemsFromResult;
import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.*;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    private final MinioClient minioClient;


    public List<ResourceResponseDto> getDirectoryInfo(String path, int userId) {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket("user-files").prefix("user-" + userId + "-files/" + path).build()
            );
            List<Item> allFiles = getItemsFromResult(results);
            if (allFiles.isEmpty()) {
                throw new ResourceNotFoundException("Directory not found: "+path);
            }
            List<ResourceResponseDto> resourceResponseDtos = new ArrayList<>();
            for (Item item : allFiles) {
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

}
