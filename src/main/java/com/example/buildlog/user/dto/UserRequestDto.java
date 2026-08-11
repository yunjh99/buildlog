package com.example.buildlog.user.dto;

import com.example.buildlog.user.domain.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    private String loginId;
    private String password;
    private String passwordConfirm;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private Role role;

    // 약관 동의 필드 추가
    private boolean agreeTerms;
    private boolean agreePrivacyPurpose;
    private boolean agreePrivacyPeriod;

}
