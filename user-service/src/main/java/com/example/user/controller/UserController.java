package com.example.user.controller;

import com.example.user.config.ApiResponse;
import com.example.user.dto.request.UserLoginRequest;
import com.example.user.dto.request.UserRegisterRequest;
import com.example.user.dto.response.AuthTokenDto;
import com.example.user.dto.response.SessionDto;
import com.example.user.dto.response.UserDto;
import com.example.user.service.UserAccountService;
import com.example.user.service.UserAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserAuthService userAuthService;       // Cho Login/Logout/Refresh
    private final UserAccountService userAccountService; // Cho Register

    // --- TASK 4: REGISTER ---
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody @Valid UserRegisterRequest request) {
        UserDto userDto = userAccountService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đăng ký thành công", userDto));
    }

    // --- TASK 3: LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenDto>> login(@RequestBody @Valid UserLoginRequest request) {
        AuthTokenDto result = userAuthService.login(request);
        ResponseCookie cookie = createRefreshTokenCookie(result.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(200, "Login thành công", result));
    }

    // --- TASK 3: REFRESH TOKEN (authenticateSession) ---
    @PostMapping("/refresh") // Mapping này ứng với hàm authenticateSession trong UML
    public ResponseEntity<ApiResponse<SessionDto>> authenticateSession(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);
        SessionDto result = userAuthService.authenticateSession(refreshToken);
        ResponseCookie cookie = createRefreshTokenCookie(result.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(200, "Refresh thành công", result));
    }

    // --- TASK 3: LOGOUT ---
    @PostMapping("/logout")
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
