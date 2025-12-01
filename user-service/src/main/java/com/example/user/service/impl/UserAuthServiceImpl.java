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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // --- 1. LOGIN ---
    @Override
    @Transactional
    public AuthTokenDto login(UserLoginRequest req) {
        // Xác thực User/Pass
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Email hoặc mật khẩu sai");
        }

        // Tìm User
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Xóa session cũ (nếu muốn mỗi user chỉ đăng nhập 1 nơi)
        sessionRepository.deleteByUser(user);

        // Tạo Token mới
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = UUID.randomUUID().toString();

        // Lưu Session
        saveSession(user, refreshToken);

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
        // Tìm session trong DB
        Session session = sessionRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "Session không tồn tại"));

        // Kiểm tra hết hạn
        if (session.getExpiryDate().isBefore(Instant.now())) {
            sessionRepository.delete(session); // Hết hạn thì xóa luôn
            throw new AppException(ErrorCode.UNAUTHORIZED, "Session đã hết hạn, vui lòng login lại");
        }

        User user = session.getUser();

        // TOKEN ROTATION (Bảo mật cao): Xóa token cũ, cấp token mới
        sessionRepository.delete(session);

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String newRefreshToken = UUID.randomUUID().toString();

        saveSession(user, newRefreshToken);

        return new SessionDto(newAccessToken, newRefreshToken);
    }

    // Hàm phụ trợ để lưu session cho gọn code
    private void saveSession(User user, String refreshToken) {
        Session session = Session.builder()
                .user(user)
                .token(refreshToken)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        sessionRepository.save(session);
    }
}
