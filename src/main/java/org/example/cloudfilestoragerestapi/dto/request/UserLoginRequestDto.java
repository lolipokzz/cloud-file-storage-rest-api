package org.example.cloudfilestoragerestapi.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserLoginRequestDto {

    private String username;

    private String password;
}
