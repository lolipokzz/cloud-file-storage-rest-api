package org.example.cloudfilestoragerestapi.config;


import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {


    @Value("${MINIO_URL}")
    private String minioUrl;

    @Value("${MINIO_USER}")
    private String minioUser;

    @Value("${MINIO_PASSWORD}")
    private String minioPassword;


    @Value("${BUCKET_NAME}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient minioClient = MinioClient.builder().endpoint(minioUrl)
                    .credentials(minioUser, minioPassword)
                    .build();

            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            return minioClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
