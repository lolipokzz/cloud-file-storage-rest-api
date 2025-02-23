package org.example.cloudfilestoragerestapi;

import org.springframework.boot.SpringApplication;

public class TestCloudFileStorageRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(CloudFileStorageRestApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
