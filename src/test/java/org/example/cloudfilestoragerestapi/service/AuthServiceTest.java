package org.example.cloudfilestoragerestapi.service;


import org.example.cloudfilestoragerestapi.dto.request.NewUserRequestDto;
import org.example.cloudfilestoragerestapi.entity.User;
import org.example.cloudfilestoragerestapi.exception.UserAlreadyExistsException;
import org.example.cloudfilestoragerestapi.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthServiceTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }


    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    public void serviceShouldRegisterNewUser() {
        NewUserRequestDto newUserDto = NewUserRequestDto.builder().username("user1").password("password1").build();
        authService.saveNewUser(newUserDto);
        Optional<User> userOptional = userRepository.findByLogin("user1");
        assertThat(userOptional).isPresent();

    }

    @Test
    public void registrationServiceShouldThrowException() {
        NewUserRequestDto newUserDto = NewUserRequestDto.builder().username("user1").password("password1").build();
        authService.saveNewUser(newUserDto);
        Assertions.assertThrows(UserAlreadyExistsException.class, ()->authService.saveNewUser(newUserDto));

    }



}
