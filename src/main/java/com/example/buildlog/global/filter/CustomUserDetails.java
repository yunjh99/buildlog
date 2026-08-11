package com.example.buildlog.global.filter;

import com.example.buildlog.user.domain.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.List;

public class CustomUserDetails extends User {
    public CustomUserDetails(String loginId, Role role) {
        super(loginId, "", List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
    }
}
