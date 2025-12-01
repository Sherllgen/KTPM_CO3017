package com.example.user.repository;

import com.example.user.model.Session;
import com.example.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByToken(String token);

    // Tìm session theo User để xóa cũ tạo mới
    Optional<Session> findByUser(User user);

    void deleteByUser(User user);
}
