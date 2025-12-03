package com.example.user.mapper.impl;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.user.dto.response.UserDto;
import com.example.user.mapper.UserMapper;
import com.example.user.model.Role;
import com.example.user.model.User;

@Component
public class UserMaperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
            .id(user.getUserId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .status(user.getStatus().toString())
            .roles(user.getRoles() != null && !user.getRoles().isEmpty() ? user.getRoles().stream()
                                        .map(Role::getName)
                                        .collect(Collectors.toList()) : null)
            .phone(user.getPhone())
            .age(user.getAge())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
