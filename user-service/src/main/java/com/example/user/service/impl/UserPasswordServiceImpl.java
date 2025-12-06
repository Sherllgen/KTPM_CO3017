package com.example.user.service.impl;

import com.example.user.dto.request.UserResetPasswordRequest;
import com.example.user.dto.request.UserUpdatePasswordRequest;
import com.example.user.exception.AppException;
import com.example.user.exception.ErrorCode;
import com.example.user.model.User;
import com.example.user.model.enums.UserStatus;
import com.example.user.repository.UserRepository;
import com.example.user.service.EmailService;
import com.example.user.service.UserPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPasswordServiceImpl implements UserPasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
     private final EmailService emailService; // Uncomment if you have email service

    @Override
    @Transactional
    public void changePassword(Long userId, UserUpdatePasswordRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new AppException(ErrorCode.NEW_PASSWORD_MISMATCH);
        }

        if (request.oldPassword().equals(request.newPassword())) {
            throw new AppException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("User {} changed password successfully", user.getEmail());
    }

    @Override
    @Transactional
    public void sendPasswordResetOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }

        String otp = generateOtp();
        user.setVerificationCode(otp);
        userRepository.save(user);

         emailService.sendPasswordResetOtp(email, otp);
        log.info("Password reset OTP sent to email: {}, OTP: {}", email, otp);
    }

    @Override
    @Transactional
    public void resetPassword(UserResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        if (!request.otp().equals(user.getVerificationCode())) {
            throw new AppException(ErrorCode.INVALID_OTP_CODE);
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setVerificationCode(null);
        userRepository.save(user);

        log.info("User {} reset password successfully", user.getEmail());
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
