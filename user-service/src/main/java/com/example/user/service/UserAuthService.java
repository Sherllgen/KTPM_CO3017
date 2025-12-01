package com.example.user.service;

import com.example.user.dto.request.UserLoginRequest;
import com.example.user.dto.response.AuthTokenDto;
import com.example.user.dto.response.SessionDto;

public interface UserAuthService {
    // 1. Login
    AuthTokenDto login(UserLoginRequest req);

    // 2. Logout (Cần biết ai logout nên tôi thêm tham số, dù UML ẩn đi)
    void logout(String refreshToken);

    // 3. Authenticate Session (Chính là Refresh Token logic)
    SessionDto authenticateSession(String refreshToken);
}
