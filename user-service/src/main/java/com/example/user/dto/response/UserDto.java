package com.example.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private String status;
    private List<String> roles;
}
