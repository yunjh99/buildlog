package com.example.buildlog.user.service;

import com.example.buildlog.user.domain.User;
import com.example.buildlog.user.domain.Role;
import com.example.buildlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.login-id}") private String loginId;
    @Value("${admin.password:}") private String password;

    @Override
    public void run(String... args) {
        if (!password.isBlank() && userRepository.findByLoginId(loginId).isEmpty()) {
            userRepository.save(new User(loginId, passwordEncoder.encode(password), Role.ADMIN));
        }
    }
}
