package com.example.buildlog.user.dto;

import com.example.buildlog.user.domain.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginUser {
    private String loginId;
    private Role role;

    @Builder
    public LoginUser(String loginId, Role role) {
        this.loginId = loginId;
        this.role = role;
    }
}
