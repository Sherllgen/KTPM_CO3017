package com.example.user.service;

import org.springframework.data.domain.Page;

import com.example.user.dto.request.UserCreateRequest;
import com.example.user.dto.request.UserRegisterRequest;
import com.example.user.dto.response.UserDto;
import com.example.user.model.enums.UserStatus;
import java.util.Set;

public interface UserAccountService {
    // 1. Hàm cho người dùng tự đăng ký (Input: UserRegisterRequest)
    UserDto register(UserRegisterRequest req);

    // 2. Hàm cho Admin tạo user (Input: UserCreateRequest - Có chọn role)
    UserDto createUserAccount(UserCreateRequest req);

    // 3. Hàm khóa/mở khóa tài khoản
    void toggleUserStatus(Long userId);

    void verifyAccount(String email, String code);

    void deleteUser(Long id);

    // 4. Lấy danh sách user (có phân trang, lọc theo role, status)
    Page<UserDto> getUsers(String role, UserStatus status, Integer page, Integer size, String sortBy, String sortDir);

    // 5. Cập nhật role cho user
    UserDto updateUserRoles(Long userId, Set<String> roleNames);
}
