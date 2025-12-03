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
        // 1. INITIALIZE ROLE (If role table is empty)
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name("ADMIN").build());
            roleRepository.save(Role.builder().name("STUDENT").build());
            roleRepository.save(Role.builder().name("INSTRUCTOR").build());

            System.out.println("-----> INITIALIZED 3 ROLES: ADMIN, STUDENT, INSTRUCTOR <-----");
        }

        // 2. INITIALIZE ADMIN USER (For testing admin user creation API)
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
            System.out.println("-----> CREATED SAMPLE USERS ADMIN, STUDENT, INSTRUCTOR: \n" +
                    "admin@gmail.com / 123456 \n" +
                    "student@gmail.com / 123456 \n" +
                    "instructor@gmail.com / 123456 <-----");
        }
    }
}
