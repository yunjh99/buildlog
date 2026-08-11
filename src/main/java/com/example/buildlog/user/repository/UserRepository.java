package com.example.buildlog.user.repository;

import com.example.buildlog.user.domain.Role;
import com.example.buildlog.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByLoginIdAndRole(String loginId, Role role);
}
