package com.example.buildlog.profile.controller;

import com.example.buildlog.global.common.SuccessResponse;
import com.example.buildlog.profile.dto.ProfileContentRequest;
import com.example.buildlog.profile.dto.ProfileContentResponse;
import com.example.buildlog.profile.service.ProfileContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileContentController {
    private final ProfileContentService service;

    @GetMapping
    public SuccessResponse<ProfileContentResponse> find() {
        return SuccessResponse.of(HttpStatus.OK, "프로필 조회 성공", service.find());
    }

    @PutMapping
    public SuccessResponse<ProfileContentResponse> update(@Valid @RequestBody ProfileContentRequest request) {
        return SuccessResponse.of(HttpStatus.OK, "프로필 수정 성공", service.update(request));
    }
}
