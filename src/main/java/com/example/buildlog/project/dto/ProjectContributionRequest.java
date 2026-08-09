package com.example.buildlog.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProjectContributionRequest(
        @NotBlank(message = "주요 작업 제목은 필수입니다.")
        @Size(max = 200, message = "주요 작업 제목은 200자 이하여야 합니다.")
        String title,

        // 하위 설명이 없는 작업은 null로 전달할 수 있다.
        @Size(max = 2000, message = "주요 작업 상세 내용은 2,000자 이하여야 합니다.")
        String detail,

        @NotNull(message = "주요 작업 표시 순서는 필수입니다.")
        @Positive(message = "주요 작업 표시 순서는 1 이상이어야 합니다.")
        Integer displayOrder
) {
}
