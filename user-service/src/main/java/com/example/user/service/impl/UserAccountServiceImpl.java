package com.example.user.service.impl;

import com.example.user.dto.request.UserCreateRequest;
import com.example.user.dto.request.UserRegisterRequest;
import com.example.user.dto.response.UserDto;
import com.example.user.exception.AppException;
import com.example.user.exception.ErrorCode;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.enums.UserStatus;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // Inject cái này để tìm Role
    private final PasswordEncoder passwordEncoder;

    // --- 1. ĐĂNG KÝ (User tự đăng ký -> Mặc định là STUDENT) ---
    @Override
    @Transactional
    public UserDto register(UserRegisterRequest req) {
        // Check email trùng
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Email đã tồn tại");
        }

        // Tìm Role STUDENT trong DB (Dynamic Role)
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Role STUDENT chưa được tạo trong DB"));

        // Tạo User
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .status(UserStatus.ACTIVE) // Enum UserStatus bạn đang dùng
                .roles(Set.of(studentRole)) // Set role vào
                .build();

        userRepository.save(user);
        return mapToUserDto(user);
    }

    // --- 2. TẠO USER (Admin tạo -> Cho chọn Role) ---
    @Override
    @Transactional
    public UserDto createUserAccount(UserCreateRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Email đã tồn tại");
        }

        Set<Role> roles = new HashSet<>();
        // Nếu Admin gửi danh sách role lên
        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            for (String roleName : req.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Role " + roleName + " không tồn tại"));
                roles.add(role);
            }
        } else {
            // Nếu không gửi gì, mặc định là STUDENT
            roles.add(roleRepository.findByName("STUDENT").orElseThrow());
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .status(UserStatus.ACTIVE)
                .roles(roles)
                .build();

        userRepository.save(user);
        return mapToUserDto(user);
    }

    // --- 3. KHÓA TÀI KHOẢN ---
    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        if (user.getStatus() == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.INACTIVE);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }
        userRepository.save(user);
    }

    // Helper map data
    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getUserId()) // Chú ý: Entity của bạn dùng 'userId' hay 'id'? Check lại file User.java
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus().name())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .build();
    }
}
