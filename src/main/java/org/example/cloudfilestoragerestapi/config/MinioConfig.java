package org.example.cloudfilestoragerestapi.config;


import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {


    @Value("${MINIO_URL}")
    String minioUrl;

    @Value("${MINIO_USER}")
    String minioUser;

    @Value("${MINIO_PASSWORD}")
    String minioPassword;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(minioUrl)
                .credentials(minioUser, minioPassword)
                .build();
    }



}
