package org.example.cloudfilestoragerestapi.service;


import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.getResourceNameWithoutPath;
import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.getResourcePathWithoutName;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;


    public void deleteFile(int userId, String path) {
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

    public ResourceResponseDto getFileInfo(int userId, String path) {
        try {
            StatObjectResponse objectStat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket("user-files")
                    .object("user-" + userId + "-files/" + path)
                    .build());

            return ResourceResponseDto.builder()
                    .name(getResourceNameWithoutPath(path))
                    .size(objectStat.size())
                    .path(path)
                    .type("FILE")
                    .build();

        } catch (Exception e) {

            throw new ResourceNotFoundException("Resource not found");
        }

    }



    public InputStream getFileAsStream(int userId, String path) {
        try {
            return minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket("user-files")
                    .object("user-" + userId + "-files/" + path)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
