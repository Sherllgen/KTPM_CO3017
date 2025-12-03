package com.example.user.service.impl;

import org.springframework.stereotype.Service;

import com.example.user.dto.request.UserProfileUpdateRequest;
import com.example.user.dto.response.UserDto;
import com.example.user.exception.AppException;
import com.example.user.exception.ErrorCode;
import com.example.user.mapper.UserMapper;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toDto(user);        
    }
    
    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAge(request.getAge());

        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
