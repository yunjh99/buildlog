package com.example.buildlog.career.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CareerActivityRequest(
        @NotBlank(message = "활동 내역은 필수입니다.")
        @Size(max = 1000, message = "활동 내역은 1,000자 이하여야 합니다.")
        String content
) {
}
