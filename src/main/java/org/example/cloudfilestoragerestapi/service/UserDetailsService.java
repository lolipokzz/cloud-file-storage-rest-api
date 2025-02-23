package org.example.cloudfilestoragerestapi.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudfilestoragerestapi.entity.User;
import org.example.cloudfilestoragerestapi.repository.UserRepository;
import org.example.cloudfilestoragerestapi.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByLogin(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return new UserDetailsImpl(userOptional.get());
    }
}