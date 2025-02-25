package org.example.cloudfilestoragerestapi.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NewUserRequestDto {

    @Size(min= 4, max = 15, message = "Username must be between 4 and 15 characters")
    @NotBlank(message = "Username cannot be blank")
    @Pattern(regexp = "^\\S+$", message = "Username must not contain spaces")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 4, max = 30, message = "Password must be more than 3 characters")
    @Pattern(regexp = "^\\S+$", message = "Password must not contain spaces")
    private String password;
}
