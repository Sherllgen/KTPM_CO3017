package com.example.user.service;

import org.springframework.data.domain.Page;

import com.example.user.dto.request.UserCreateRequest;
import com.example.user.dto.request.UserRegisterRequest;
import com.example.user.dto.response.UserDto;
import com.example.user.dto.response.UserValidationDto;
import com.example.user.model.enums.UserStatus;
import java.util.Set;

public interface UserAccountService {
    // 1. Function for user self-registration (Input: UserRegisterRequest)
    UserDto register(UserRegisterRequest req);

    // 2. Function for Admin to create user (Input: UserCreateRequest - With role
    // selection)
    UserDto createUserAccount(UserCreateRequest req);

    // 3. Function to lock/unlock account
    void toggleUserStatus(Long userId);

    void verifyAccount(String email, String code);

    void deleteUser(Long id);

    // 4. Get user list (with pagination, filter by role, status)
    Page<UserDto> getUsers(String role, UserStatus status, Integer page, Integer size, String sortBy, String sortDir);

    // 5. Update role for user
    UserDto updateUserRoles(Long userId, Set<String> roleNames);

    // 6. Validate instructor for inter-service communication
    UserValidationDto validateInstructor(Long userId);

    void resendVerificationCode(String email);
}
