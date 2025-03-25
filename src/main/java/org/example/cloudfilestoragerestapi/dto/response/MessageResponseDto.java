package org.example.cloudfilestoragerestapi.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MessageResponseDto {
    private String message;
}
