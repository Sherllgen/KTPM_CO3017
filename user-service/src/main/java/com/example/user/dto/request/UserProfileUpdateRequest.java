package com.example.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {
    private String fullName;
    private String phone;

    @Min(value = 1, message = "Age must be greater than 0")
    @Max(value = 150, message = "Age must be less than 150")
    private Integer age;
}
