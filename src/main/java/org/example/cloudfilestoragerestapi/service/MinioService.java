package org.example.cloudfilestoragerestapi.service;


import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.exception.ResourceNotFoundException;
import org.example.cloudfilestoragerestapi.exception.UploadException;
import org.example.cloudfilestoragerestapi.util.ItemMapper;
import org.example.cloudfilestoragerestapi.util.ResourceNamingUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    private final ItemMapper itemMapper;

    private final ResourceNamingUtil resourceNamingUtil;

    @Value("${BUCKET_NAME}")
    private String bucketName;


    @Value("${EMPTY_FILE}")
    private String emptyFile;


    public List<Item> getItemsFromMinio(int userId, String path) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(resourceNamingUtil.getUserRootFolder(userId) + path)
                        .build()
        );
        return itemMapper.getItemsFromResult(results);
    }


    public List<Item> getItemsFromMinioRecursively(int userId, String path) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(resourceNamingUtil.getUserRootFolder(userId) + path)
                        .recursive(true)
                        .build()
        );
        return itemMapper.getItemsFromResult(results);
    }


    public InputStream getObject(String path) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .build());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }


    public void removeObject(String path) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .build());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }


    public long getObjectSize(String path) {
        try {
            StatObjectResponse objectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)

                    .object(path)
                    .build());

            return  objectResponse.size();
        } catch (Exception e) {

            throw new ResourceNotFoundException("Resource not found");
        }
    }


    public void putEmptyObject(String path) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path + emptyFile)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    public void copyObject(String sourcePath, String targetPath) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(targetPath)
                            .source(
                                    CopySource.builder()
                                            .bucket(bucketName)
                                            .object(sourcePath)
                                            .build())
                            .build());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }


    public void putObject(String path, int userId, MultipartFile file) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(resourceNamingUtil.getUserRootFolder(userId) + path)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build());

        } catch (Exception e) {
            throw new UploadException("Something went wrong");
        }
    }

}
