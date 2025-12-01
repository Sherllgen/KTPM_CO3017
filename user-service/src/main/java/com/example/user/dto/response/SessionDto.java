package com.example.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionDto {
    private String accessToken;
    // Có thể trả thêm refreshToken mới nếu bạn muốn xoay vòng token liên tục (Rotation)
    private String refreshToken;
}
