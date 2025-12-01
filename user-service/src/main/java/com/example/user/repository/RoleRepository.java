package com.example.user.repository;

import com.example.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Hàm này giúp tìm Role bằng tên (VD: tìm chữ "STUDENT" hoặc "ADMIN")
    Optional<Role> findByName(String name);
}
