package com.example.user.controller;

import com.example.user.config.ApiResponse;
import com.example.user.dto.request.UserProfileUpdateRequest;
import com.example.user.dto.response.UserDto;
import com.example.user.service.UserAccountService;
import com.example.user.service.UserAuthService;
import com.example.user.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.example.user.config.jwt.SecurityUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    private final UserAuthService userAuthService;
    private final UserAccountService userAccountService;

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

    // --- TASK 3: LOGOUT ---
    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất", description = "Xóa Session trong DB và xóa Cookie Refresh Token ở trình duyệt")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);
        userAuthService.logout(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(200, "Logout thành công", null));
    }

    @PostMapping("/verify")
    @Operation(summary = "Xác thực email", description = "Nhập email và mã code 6 số nhận được để kích hoạt tài khoản")
    public ResponseEntity<ApiResponse<Void>> verifyAccount(@RequestParam String email, @RequestParam String code) {
        userAccountService.verifyAccount(email, code);
        return ResponseEntity.ok(new ApiResponse<>(200, "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập.", null));
    }

    // --- Helper Methods ---
    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false) // Deploy để true
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new RuntimeException("Refresh Token không tìm thấy trong Cookie");
    }
}
