package com.example.user.service;

import com.example.user.dto.request.UserResetPasswordRequest;
import com.example.user.dto.request.UserUpdatePasswordRequest;

public interface UserPasswordService {
    void changePassword(Long userId, UserUpdatePasswordRequest request);
    void sendPasswordResetOtp(String email);
    void resetPassword(UserResetPasswordRequest request);
}
