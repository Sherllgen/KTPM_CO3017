package com.example.user.service;

import com.example.user.dto.request.UserCreateRequest;
import com.example.user.dto.request.UserRegisterRequest;
import com.example.user.dto.response.UserDto;

public interface UserAccountService {
    // 1. Hàm cho người dùng tự đăng ký (Input: UserRegisterRequest)
    UserDto register(UserRegisterRequest req);

    // 2. Hàm cho Admin tạo user (Input: UserCreateRequest - Có chọn role)
    UserDto createUserAccount(UserCreateRequest req);

    // 3. Hàm khóa/mở khóa tài khoản
    void toggleUserStatus(Long userId);

    void verifyAccount(String email, String code);
}
