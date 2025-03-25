package org.example.cloudfilestoragerestapi.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.cloudfilestoragerestapi.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
@Getter
@Setter
public class UserDetailsImpl implements UserDetails, Serializable {

    private final User user;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getLogin();
    }


    public User getUser() {
        return user;
    }
}