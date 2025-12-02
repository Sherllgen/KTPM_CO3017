package com.example.user.service.impl;

import com.example.user.config.jwt.CustomUserDetails;
import com.example.user.exception.AppException;
import com.example.user.exception.ErrorCode;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.enums.UserStatus;
import com.example.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
        private final UserRepository userRepository;

        @Override
        @Transactional
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                Set<String> roles = user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet());

                boolean enabled = (user.getStatus() == UserStatus.ACTIVE);

                return CustomUserDetails.builder()
                                .id(user.getUserId())
                                .email(user.getEmail())
                                .password(user.getPassword())
                                .enabled(enabled)
                                .roles(roles)
                                .build();
        }
}
