package com.example.user.service;

import com.example.user.dto.request.UserLoginRequest;
import com.example.user.dto.response.AuthTokenDto;
import com.example.user.dto.response.SessionDto;

public interface UserAuthService {
    // 1. Login
    AuthTokenDto login(UserLoginRequest req);

    // 2. Logout (Need to know who logs out, so I added a parameter, even if UML
    // hides it)
    void logout(String refreshToken);

    // 3. Authenticate Session (This is Refresh Token logic)
    SessionDto authenticateSession(String refreshToken);
}
