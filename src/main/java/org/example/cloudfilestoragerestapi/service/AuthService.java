package org.example.cloudfilestoragerestapi.service;


import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.dto.request.NewUserRequestDto;
import org.example.cloudfilestoragerestapi.dto.response.UserResponseDto;
import org.example.cloudfilestoragerestapi.entity.User;
import org.example.cloudfilestoragerestapi.exception.UserAlreadyExistsException;
import org.example.cloudfilestoragerestapi.repository.UserRepository;
import org.example.cloudfilestoragerestapi.security.UserDetailsImpl;
import org.example.cloudfilestoragerestapi.util.ResourceNamingUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final MinioService minioService;

    private final ResourceNamingUtil resourceNamingUtil;

    private final UserDetailsService userDetailsService;

    @Transactional
    public UserResponseDto saveNewUser(NewUserRequestDto newUserRequestDto) {


        String password = passwordEncoder.encode(newUserRequestDto.getPassword());
        User user = User.builder().login(newUserRequestDto.getUsername()).password(password).build();

        try {
            userRepository.save(user);
            UserDetailsImpl userDetails =(UserDetailsImpl) userDetailsService.loadUserByUsername(user.getLogin());
            String rootFolder = resourceNamingUtil.getUserRootFolder(userDetails.getUser().getId());
            minioService.putEmptyObject(rootFolder);


        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("User already exists");
        }

        return UserResponseDto.builder().username(user.getLogin()).build();
    }
}
