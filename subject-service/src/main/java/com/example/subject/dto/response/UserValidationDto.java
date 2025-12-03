package com.example.subject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationDto {
    private Long userId;
    private String email;
    private String fullName;
    private Boolean isInstructor;
    private Boolean isActive;
}
