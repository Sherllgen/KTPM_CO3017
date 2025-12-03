package com.example.user.repository;

import com.example.user.model.User;
import com.example.user.model.enums.UserStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;

public class UserSpecifications {

    public static Specification<User> hasRole(String roleName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(roleName)) {
                return null;
            }
            Join<Object, Object> roles = root.join("roles");
            return criteriaBuilder.equal(roles.get("name"), roleName);
        };
    }

    public static Specification<User> hasStatus(UserStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
