package com.example.buildlog.career.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CareerRoleRequest(
        @NotBlank(message = "역할명은 필수입니다.")
        @Size(max = 100, message = "역할명은 100자 이하여야 합니다.")
        String title,

        @NotNull(message = "활동 내역 목록은 필수입니다.")
        @Size(min = 1, message = "역할마다 활동 내역을 하나 이상 입력해야 합니다.")
        List<@Valid CareerActivityRequest> activities
) {
}
