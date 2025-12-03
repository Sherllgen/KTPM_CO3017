package com.example.subject.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.subject.config.ApiResponse;
import com.example.subject.dto.response.UserValidationDto;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/internal/users/{userId}/validate-instructor")
    ResponseEntity<ApiResponse<UserValidationDto>> validateInstructor(@PathVariable("userId") Long userId);
}
