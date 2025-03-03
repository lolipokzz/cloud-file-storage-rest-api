package org.example.cloudfilestoragerestapi.service;


import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.cloudfilestoragerestapi.util.ItemUtil.getItemsFromResult;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    public List<Item> getItemsFromMinio(int userId, String path) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket("user-files")
                        .prefix("user-" + userId + "-files/" + path)
                        .build()
        );
        return getItemsFromResult(results);
    }


    public List<Item> getItemsFromMinioRecursively(int userId, String path) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket("user-files")
                        .prefix("user-" + userId + "-files/" + path)
                        .recursive(true)
                        .build()
        );
        return getItemsFromResult(results);
    }


}
