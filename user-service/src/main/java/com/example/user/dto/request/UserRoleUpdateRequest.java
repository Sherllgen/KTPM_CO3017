package com.example.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UserRoleUpdateRequest {
    @NotEmpty(message = "Danh sách role không được để trống")
    private Set<String> roles;
}
