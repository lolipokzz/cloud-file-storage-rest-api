package org.example.cloudfilestoragerestapi.service;


import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.request.NewUserRequestDto;
import org.example.cloudfilestoragerestapi.dto.response.UserResponseDto;
import org.example.cloudfilestoragerestapi.entity.User;
import org.example.cloudfilestoragerestapi.exception.UserAlreadyExistsException;
import org.example.cloudfilestoragerestapi.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Transactional
    public UserResponseDto saveNewUser(NewUserRequestDto newUserRequestDto) {
        newUserRequestDto.setPassword(passwordEncoder.encode(newUserRequestDto.getPassword()));
        User user = User.builder().login(newUserRequestDto.getUsername()).password(newUserRequestDto.getPassword()).build();
        try {
            userRepository.save(user);
        }catch (DataIntegrityViolationException e){
            throw new UserAlreadyExistsException("User already exists");
        }

        return UserResponseDto.builder().username(user.getLogin()).build();
    }
}
