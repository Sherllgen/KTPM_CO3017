package com.example.user.controller;

import com.example.user.config.ApiResponse;
import com.example.user.dto.request.UserCreateRequest;
import com.example.user.dto.response.UserDto;
import com.example.user.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserAccountService userAccountService;

    // --- TASK 4: CREATE USER (Admin tạo) ---
    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUserAccount(@RequestBody @Valid UserCreateRequest request) {
        UserDto userDto = userAccountService.createUserAccount(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Admin tạo User thành công", userDto));
    }

    // --- TASK 4: TOGGLE STATUS (Khóa/Mở khóa) ---
    @PutMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable Long userId) {
        userAccountService.toggleUserStatus(userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đổi trạng thái tài khoản thành công", null));
    }
}
