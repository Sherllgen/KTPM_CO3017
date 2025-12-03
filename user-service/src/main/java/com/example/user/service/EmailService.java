package com.example.user.service;

public interface EmailService {
    void sendVerificationEmail(String to, String subject, String code);
    void sendPasswordResetOtp(String to, String otp);
}
