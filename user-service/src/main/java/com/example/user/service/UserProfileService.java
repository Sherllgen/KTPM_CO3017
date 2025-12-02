package com.example.user.service;

import com.example.user.dto.request.UserProfileUpdateRequest;
import com.example.user.dto.response.UserDto;

public interface UserProfileService {
    UserDto getProfile(Long userId);
    UserDto updateUser(Long userId, UserProfileUpdateRequest request);
}
