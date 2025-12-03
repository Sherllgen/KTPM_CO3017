package com.example.user.controller;

import com.example.user.config.ApiResponse;
import com.example.user.dto.request.UserCreateRequest;
import com.example.user.dto.request.UserRoleUpdateRequest;
import com.example.user.dto.response.UserDto;
import com.example.user.model.enums.UserStatus;
import com.example.user.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Các API hỗ trợ Admin quản lý User")
public class UserAdminController {
    private final UserAccountService userAccountService;

    // --- CREATE USER (Admin tạo) ---
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo tài khoản mới (Admin)", description = "Admin tạo user mới và cấp quyền cụ thể (INSTRUCTOR, ADMIN...)")
    public ResponseEntity<ApiResponse<UserDto>> createUserAccount(@RequestBody @Valid UserCreateRequest request) {
        UserDto userDto = userAccountService.createUserAccount(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Admin tạo User thành công", userDto));
    }

    // --- TOGGLE STATUS (Khóa/Mở khóa) ---
    @PutMapping("/{userId}/status")
    @Operation(summary = "Khóa/Mở khóa tài khoản", description = "Chuyển đổi trạng thái user giữa ACTIVE và INACTIVE")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable Long userId) {
        userAccountService.toggleUserStatus(userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đổi trạng thái tài khoản thành công", null));
    }

    // --- DELETE USER ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa người dùng", description = "Xóa người dùng")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userAccountService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Xóa người dùng thành công", null));
    }

    // --- GET USERS (List, Filter, Pageable) ---
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách user", description = "Lấy danh sách user có phân trang, lọc theo role và status")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getUsers(
        @RequestParam(required = false) String role,
        @RequestParam(required = false) UserStatus status,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam(required = false, defaultValue = "userId") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String sortDir
    ) {
        Page<UserDto> users = userAccountService.getUsers(role, status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách user thành công", users));
    }

    // --- UPDATE USER ROLES ---
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật role cho user", description = "Thay thế danh sách role hiện tại của user bằng danh sách mới")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRoles(
            @PathVariable Long id,
            @RequestBody @Valid UserRoleUpdateRequest request) {
        UserDto userDto = userAccountService.updateUserRoles(id, request.getRoles());
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật role thành công", userDto));
    }
}
