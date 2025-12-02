package com.example.user.service.impl;

import jakarta.mail.MessagingException;
import com.example.user.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String to, String subject, String code) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = "<h3>Chào mừng đến với ITS!</h3>"
                    + "<p>Mã xác thực của bạn là: <b style='font-size: 20px; color: blue;'>" + code + "</b></p>"
                    + "<p>Vui lòng nhập mã này để kích hoạt tài khoản.</p>";

            helper.setText(htmlMsg, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("ITS Support <noreply@travelez.com>");

            mailSender.send(mimeMessage);
            log.info("Email xác thực đã gửi đến: {}", to);
        } catch (MessagingException e) {
            log.error("Lỗi gửi email", e);
            throw new RuntimeException("Không thể gửi email xác thực");
        }
    }
}
