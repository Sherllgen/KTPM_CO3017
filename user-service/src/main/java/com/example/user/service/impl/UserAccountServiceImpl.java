package com.example.user.service.impl;

import com.example.user.dto.request.UserCreateRequest;
import com.example.user.dto.request.UserRegisterRequest;
import com.example.user.dto.response.UserDto;
import com.example.user.dto.response.UserValidationDto;
import com.example.user.exception.AppException;
import com.example.user.exception.ErrorCode;
import com.example.user.mapper.UserMapper;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.enums.UserStatus;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
import com.example.user.repository.UserSpecifications;
import com.example.user.service.EmailService;
import com.example.user.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserMapper userMapper;

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

        // Tạo mã xác thực ngẫu nhiên 6 chữ số
        String code = String.valueOf((int) ((Math.random() * 899999) + 100000));

        // Tạo User
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .status(UserStatus.INACTIVE)
                .roles(Set.of(studentRole)) // Set role vào
                .verificationCode(code) // Lưu mã xác thực
                .build();

        userRepository.save(user);

        emailService.sendVerificationEmail(req.getEmail(), "Xác thực tài khoản", code);

        return userMapper.toDto(user);
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
                        .orElseThrow(
                                () -> new AppException(ErrorCode.NOT_FOUND, "Role " + roleName + " không tồn tại"));
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
                .phone(req.getPhone())
                .age(req.getAge())
                .build();

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    // --- 3. KHÓA TÀI KHOẢN ---
    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.INACTIVE);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }
        userRepository.save(user);
    }

    // --- 4. IMPLEMENT HÀM VERIFY ---
    @Override
    @Transactional
    public void verifyAccount(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Tài khoản đã được kích hoạt rồi");
        }

        if (code == null || !code.equals(user.getVerificationCode())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Mã xác thực không đúng");
        }

        // Kích hoạt
        user.setStatus(UserStatus.ACTIVE);
        user.setVerificationCode(null); // Xóa mã đi dùng 1 lần thôi
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsers(String role, UserStatus status, Integer page, Integer size, String sortBy,
            String sortDir) {

        String sortField = (sortBy == null || sortBy.isBlank()) ? "userId" : sortBy.trim();
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        int pageSize = (size == null || size <= 0) ? 10 : size;
        int pageIndex = (page == null || page < 1) ? 0 : page - 1;

        PageRequest pageable = PageRequest.of(pageIndex, pageSize, Sort.by(direction, sortField));

        Specification<User> spec = UserSpecifications.hasRole(role)
                .and(UserSpecifications.hasStatus(status));

        return userRepository.findAll(spec, pageable).map(userMapper::toDto);
    }

    @Override
    @Transactional
    public UserDto updateUserRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Set<Role> roles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(
                                () -> new AppException(ErrorCode.NOT_FOUND, "Role " + roleName + " không tồn tại"));
                roles.add(role);
            }
        }

        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserValidationDto validateInstructor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean isInstructor = user.getRoles().stream()
                .anyMatch(role -> "INSTRUCTOR".equals(role.getName()));

        boolean isActive = user.getStatus() == UserStatus.ACTIVE;

        return UserValidationDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isInstructor(isInstructor)
                .isActive(isActive)
                .build();
    }
}
