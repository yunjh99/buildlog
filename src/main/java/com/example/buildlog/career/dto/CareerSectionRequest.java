package com.example.buildlog.career.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CareerSectionRequest(
        @NotBlank(message = "섹션 제목은 필수입니다.")
        @Size(max = 100, message = "섹션 제목은 100자 이하여야 합니다.")
        String title,
        @NotNull(message = "활동 목록은 필수입니다.")
        @Size(min = 1, message = "섹션마다 활동을 하나 이상 입력해야 합니다.")
        List<@NotNull @Valid CareerActivityRequest> activities
) {
}
