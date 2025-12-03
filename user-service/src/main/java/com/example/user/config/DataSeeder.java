package com.example.user.config;

import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.enums.UserStatus;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. KHOI TAO ROLE (Nếu bảng role trống)
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name("ADMIN").build());
            roleRepository.save(Role.builder().name("STUDENT").build());
            roleRepository.save(Role.builder().name("INSTRUCTOR").build());

            System.out.println("-----> ĐÃ KHỞI TẠO 3 ROLE: ADMIN, STUDENT, INSTRUCTOR <-----");
        }

        // 2. KHOI TAO ADMIN USER (Để bạn test API tạo user của admin)
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
            Role studentRole = roleRepository.findByName("STUDENT").orElse(null);
            Role instructorRole = roleRepository.findByName("INSTRUCTOR").orElse(null);

            User admin = User.builder()
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Super Admin")
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(adminRole))
                    .build();
            
            User student = User.builder()
                    .email("student@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Student")
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(studentRole))
                    .build();
            
            User instructor = User.builder()
                    .email("instructor@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Instructor")
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(instructorRole))
                    .build();

            userRepository.saveAll(List.of(admin, student, instructor));
            System.out.println("-----> ĐÃ TẠO USER ADMIN, STUDENT, INSTRUCTOR MẪU: \n" +
                    "admin@gmail.com / 123456 \n" +
                    "student@gmail.com / 123456 \n" +
                    "instructor@gmail.com / 123456 <-----");
        }
    }
}
