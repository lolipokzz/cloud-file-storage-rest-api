package org.example.cloudfilestoragerestapi.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResourceResponseDto {

    private String path;

    private String name;

    private long size;

    private String type;
}
