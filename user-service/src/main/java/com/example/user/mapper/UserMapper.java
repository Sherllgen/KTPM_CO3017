package com.example.user.mapper;

import com.example.user.dto.response.UserDto;
import com.example.user.model.User;

public interface UserMapper {
    UserDto toDto(User user);
}
