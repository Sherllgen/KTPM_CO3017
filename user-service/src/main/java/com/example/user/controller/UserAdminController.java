package com.example.user.controller;

import com.example.user.config.ApiResponse;
import com.example.user.dto.request.UserCreateRequest;
import com.example.user.dto.response.UserDto;
import com.example.user.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Các API hỗ trợ Admin quản lý User")
public class UserAdminController {
    private final UserAccountService userAccountService;

    // --- TASK 4: CREATE USER (Admin tạo) ---
    @PostMapping
    @Operation(summary = "Tạo tài khoản mới (Admin)", description = "Admin tạo user mới và cấp quyền cụ thể (TEACHER, ADMIN...)")
    public ResponseEntity<ApiResponse<UserDto>> createUserAccount(@RequestBody @Valid UserCreateRequest request) {
        UserDto userDto = userAccountService.createUserAccount(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Admin tạo User thành công", userDto));
    }

    // --- TASK 4: TOGGLE STATUS (Khóa/Mở khóa) ---
    @PutMapping("/{userId}/status")
    @Operation(summary = "Khóa/Mở khóa tài khoản", description = "Chuyển đổi trạng thái user giữa ACTIVE và INACTIVE")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable Long userId) {
        userAccountService.toggleUserStatus(userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đổi trạng thái tài khoản thành công", null));
    }

        // --- DELETE USER ---
    // @DeleteMapping("/{id}")
    // @Operation(summary = "Xóa người dùng", description = "Xóa người dùng")
    // public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
    //     userAccountService.deleteUser(id);
    //     return ResponseEntity.ok(new ApiResponse<>(200, "Xóa người dùng thành công", null));
    // }
}
