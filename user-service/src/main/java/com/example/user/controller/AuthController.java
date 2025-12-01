package com.example.user.controller;

import com.example.user.config.ApiResponse;
import com.example.user.dto.request.UserLoginRequest;
import com.example.user.dto.response.AuthTokenDto;
import com.example.user.dto.response.SessionDto;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserAuthService userAuthService;


    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenDto>> login(@RequestBody @Valid UserLoginRequest request) {
        AuthTokenDto result = userAuthService.login(request);

        // Lưu Refresh Token vào HttpOnly Cookie
        ResponseCookie cookie = createRefreshTokenCookie(result.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(200, "Login thành công", result));
    }

    // AUTHENTICATE SESSION (REFRESH)
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<SessionDto>> refresh(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);

        // Gọi hàm authenticateSession như UML yêu cầu
        SessionDto result = userAuthService.authenticateSession(refreshToken);

        // Cấp lại Cookie mới (Rotation)
        ResponseCookie cookie = createRefreshTokenCookie(result.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(200, "Refresh thành công", result));
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);

        userAuthService.logout(refreshToken);

        // Xóa Cookie phía Client
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0) // Hết hạn ngay lập tức
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(200, "Logout thành công", null));
    }

    // --- Hàm phụ trợ ---
    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false) // Deploy thì để true
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
