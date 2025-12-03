package com.example.user.controller;

import com.example.user.config.ApiResponse;
import com.example.user.config.jwt.SecurityUtil;
import com.example.user.dto.request.UserLoginRequest;
import com.example.user.dto.request.UserRegisterRequest;
import com.example.user.dto.request.UserResetPasswordRequest;
import com.example.user.dto.request.UserUpdatePasswordRequest;
import com.example.user.dto.response.AuthTokenDto;
import com.example.user.dto.response.SessionDto;
import com.example.user.dto.response.UserDto;
import com.example.user.service.UserAccountService;
import com.example.user.service.UserAuthService;
import com.example.user.service.UserPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for Registration, Login, Refresh Token for users")
public class AuthController {
    private final UserAuthService userAuthService;
    private final UserAccountService userAccountService;
    private final UserPasswordService userPasswordService;

    // --- REGISTER ---
    @PostMapping("/register")
    @Operation(summary = "Register Account", description = "User self-registers a new account (Default Role is STUDENT)")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody @Valid UserRegisterRequest request) {
        UserDto userDto = userAccountService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Registration successful", userDto));
    }

    // --- LOGIN ---
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate email/password. Returns Access Token (Body) and Refresh Token (HttpOnly Cookie)")
    public ResponseEntity<ApiResponse<AuthTokenDto>> login(@RequestBody @Valid UserLoginRequest request) {
        AuthTokenDto result = userAuthService.login(request);
        ResponseCookie cookie = createRefreshTokenCookie(result.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(200, "Login successful", result));
    }

    // --- REFRESH TOKEN (authenticateSession) ---
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Issue a new Access Token based on a valid Refresh Token in Cookie")
    public ResponseEntity<ApiResponse<SessionDto>> authenticateSession(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);
        SessionDto result = userAuthService.authenticateSession(refreshToken);
        ResponseCookie cookie = createRefreshTokenCookie(result.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(200, "Refresh successful", result));
    }

    // --- LOGOUT ---
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Delete Session in DB and delete Refresh Token Cookie in browser")
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
                .body(new ApiResponse<>(200, "Logout successful", null));
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
        throw new RuntimeException("Refresh Token not found in Cookie");
    }
    @PostMapping("/verify")
    @Operation(summary = "Xác thực email", description = "Nhập email và mã code 6 số nhận được để kích hoạt tài khoản")
    public ResponseEntity<ApiResponse<Void>> verifyAccount(@RequestParam String email, @RequestParam String code) {
        userAccountService.verifyAccount(email, code);
        return ResponseEntity.ok(new ApiResponse<>(200, "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập.", null));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change password", description = "User changes their password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody @Valid UserUpdatePasswordRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        userPasswordService.changePassword(currentUserId, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Change password successfully", null));
    }

    @PostMapping("/me/password/forgot")
    @Operation(summary = "Forget password", description = "Send OTP to email for password reset")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        userPasswordService.sendPasswordResetOtp(email);
        return ResponseEntity.ok(new ApiResponse<>(200, "OTP code has been sent", null));
    }

    @PostMapping("/me/password/reset")
    @Operation(summary = "Reset password", description = "Reset password using OTP code")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid UserResetPasswordRequest request) {
        userPasswordService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Reset password successfully", null));
    }
}
