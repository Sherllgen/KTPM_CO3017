package com.example.user.service.impl;

import com.example.user.config.jwt.JwtTokenProvider;
import com.example.user.dto.request.UserLoginRequest;
import com.example.user.dto.response.AuthTokenDto;
import com.example.user.dto.response.SessionDto;
import com.example.user.exception.AppException;
import com.example.user.exception.ErrorCode;
import com.example.user.model.Session;
import com.example.user.model.User;
import com.example.user.repository.SessionRepository;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // --- 1. LOGIN ---
    @Override
    @Transactional
    public AuthTokenDto login(UserLoginRequest req) {
        // ... (Đoạn xác thực authenticateManager giữ nguyên) ...

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // TẠO TOKEN
        Set<String> roles = user.getRoles().stream()
                .map(com.example.user.model.Role::getName)
                .collect(Collectors.toSet());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getEmail(), roles);
        String refreshToken = UUID.randomUUID().toString();

        // XỬ LÝ SESSION (Sửa đoạn này)
        // Tìm xem user này đã có session chưa?
        Session session = sessionRepository.findByUser(user)
                .orElseGet(() -> Session.builder().user(user).build()); // Nếu chưa có thì tạo vỏ mới

        // Cập nhật thông tin (Dù mới hay cũ đều set lại token và ngày hết hạn)
        session.setToken(refreshToken);
        session.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));

        sessionRepository.save(session);

        return new AuthTokenDto(accessToken, refreshToken);
    }

    // --- 2. LOGOUT ---
    @Override
    @Transactional
    public void logout(String refreshToken) {
        // Tìm session và xóa nó đi
        sessionRepository.findByToken(refreshToken)
                .ifPresent(session -> sessionRepository.delete(session));
    }

    // --- 3. AUTHENTICATE SESSION (REFRESH TOKEN) ---
    @Override
    @Transactional
    public SessionDto authenticateSession(String refreshToken) {
        // 1. Tìm session theo token cũ
        Session session = sessionRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "Session không tồn tại"));

        // 2. Kiểm tra hết hạn
        if (session.getExpiryDate().isBefore(Instant.now())) {
            sessionRepository.delete(session);
            throw new AppException(ErrorCode.UNAUTHORIZED, "Session đã hết hạn, vui lòng login lại");
        }

        User user = session.getUser();

        // 3. TOKEN ROTATION (Sửa lại đoạn này)
        // Thay vì xóa cũ tạo mới, ta cập nhật trực tiếp
        Set<String> roles = user.getRoles().stream()
                .map(com.example.user.model.Role::getName)
                .collect(Collectors.toSet());
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getEmail(), roles);
        String newRefreshToken = UUID.randomUUID().toString();

        // Cập nhật thông tin mới vào session cũ
        session.setToken(newRefreshToken);
        session.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));

        // Lưu lại (Hibernate sẽ tự hiểu là UPDATE vì session đã có ID)
        sessionRepository.save(session);

        return new SessionDto(newAccessToken, newRefreshToken);
    }

    // // Hàm phụ trợ để lưu session cho gọn code
    // private void saveSession(User user, String refreshToken) {
    // Session session = Session.builder()
    // .user(user)
    // .token(refreshToken)
    // .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
    // .build();
    // sessionRepository.save(session);
    // }
}
