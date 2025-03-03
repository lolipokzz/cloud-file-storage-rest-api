package org.example.cloudfilestoragerestapi.service;


import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.cloudfilestoragerestapi.dto.response.ResourceResponseDto;
import org.example.cloudfilestoragerestapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.example.cloudfilestoragerestapi.util.ItemUtil.getItemsFromResult;
import static org.example.cloudfilestoragerestapi.util.ResourceNamingUtil.*;

@Service
@RequiredArgsConstructor
public class DirectoryService {

    private final MinioClient minioClient;

    private final MinioService minioService;



    public List<ResourceResponseDto> getDirectoryResources(String path, int userId) {
            List<Item> allFiles = minioService.getItemsFromMinio(userId, path);
            if (allFiles.isEmpty()) {
                throw new ResourceNotFoundException("Resource not found");
            }
            List<ResourceResponseDto> resourceResponseDtos = new ArrayList<>();
            for (Item item : allFiles) {
                if (getResourceNameWithoutPath(item.objectName()).equals("empty-file")){
                    continue;
                }
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


    public void deleteDirectory(int userId, String path) {
        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId,path);
        for (Item item : allFiles) {
            try{
                minioClient.removeObject(RemoveObjectArgs.builder().bucket("user-files").object(item.objectName()).build());
            }catch (Exception e){
                throw new ResourceNotFoundException("Resource not found");
            }

        }
    }



    public ResourceResponseDto getDirectoryInfo(int userId, String path) {
        List<Item> allFiles = minioService.getItemsFromMinio(userId, path);
        if (allFiles.isEmpty()) {
            throw new ResourceNotFoundException("Directory not found: " + path);
        }
        return ResourceResponseDto.builder()
                .type("DIRECTORY")
                .path(path)
                .name(getResourceNameWithoutPath(path))
                .size(0).build();
    }




/*    public InputStream getDirectoryAsStream(int userId, String path) {
        List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, path);
        List<InputStream> inputStreams = new ArrayList<>();
        for (Item item : allFiles) {
            try {
                inputStreams.add(minioClient.getObject(GetObjectArgs
                        .builder()
                        .bucket("user-files")
                        .object(item.objectName())
                        .build()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return new SequenceInputStream(Collections.enumeration(inputStreams));
    }*/
public InputStream getDirectoryAsStream(int userId, String path) {
    // Получаем список всех файлов рекурсивно
    List<Item> allFiles = minioService.getItemsFromMinioRecursively(userId, path);

    // Создаём поток для записи ZIP в память
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ZipOutputStream zos = new ZipOutputStream(baos)) {
        for (Item item : allFiles) {
            String objectName = item.objectName(); // Путь объекта в MinIO (относительный)
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("user-files")
                            .object(objectName)
                            .build())) {
                ZipEntry zipEntry = new ZipEntry(directoryInnerPath(objectName,path));
                zos.putNextEntry(zipEntry);
                IOUtils.copy(inputStream, zos);
                zos.closeEntry();
            } catch (Exception e) {
                throw new RuntimeException("Failed to process object: " + objectName, e);
            }
        }
    } catch (IOException e) {
        throw new RuntimeException("Failed to create ZIP archive", e);
    }

    return new ByteArrayInputStream(baos.toByteArray());
}


    private String directoryInnerPath(String fullPath,String name){
        String name1 = getResourceNameWithoutPath(name);
        int startIndex = fullPath.indexOf(name1);
        return fullPath.substring(startIndex);
    }


}
