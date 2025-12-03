package com.example.user.controller;

import com.example.user.config.ApiResponse;
import com.example.user.dto.request.UserProfileUpdateRequest;
import com.example.user.dto.response.UserDto;
import com.example.user.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.example.user.config.jwt.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Profile Management", description = "Các API Quản lý thông tin người dùng")
public class UserController {
    private final UserProfileService userProfileService;

    // --- GET USER PROFILE ---
    @GetMapping("/me")
    @Operation(summary = "Lấy thông tin", description = "Người dùng lấy thông tin của bản thân")
    public ResponseEntity<ApiResponse<UserDto>> getProfile() {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        UserDto user = userProfileService.getProfile(currentUserId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin người dùng thành công", user));
    }

    // --- UPDATE USER PROFILE ---
    @PutMapping("/me")
    @Operation(summary = "Cập nhật thông tin người dùng", description = "Cập nhật thông tin người dùng")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@RequestBody @Valid UserProfileUpdateRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        UserDto user = userProfileService.updateUser(currentUserId, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật thông tin người dùng thành công", user));
    }
}
