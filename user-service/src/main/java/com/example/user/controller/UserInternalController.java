package com.example.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.config.ApiResponse;
import com.example.user.dto.response.UserValidationDto;
import com.example.user.service.UserAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/users")
@Slf4j
@Tag(name = "User Internal Controller", description = "Internal APIs for inter-service communication")
public class UserInternalController {
    private final UserAccountService userAccountService;

    @GetMapping("/{userId}/validate-instructor")
    @Operation(summary = "Validate if user is an instructor", description = "Internal endpoint to validate if a user exists and has INSTRUCTOR role. Used by other microservices.")
    public ResponseEntity<ApiResponse<UserValidationDto>> validateInstructor(@PathVariable Long userId) {
        log.info("Validating instructor with userId: {}", userId);

        UserValidationDto result = userAccountService.validateInstructor(userId);

        return ResponseEntity.ok(ApiResponse.<UserValidationDto>builder()
                .status(200)
                .data(result)
                .message("User validation completed")
                .build());
    }
}
