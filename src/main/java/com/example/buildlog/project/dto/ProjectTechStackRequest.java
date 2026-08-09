package com.example.buildlog.project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProjectTechStackRequest(
        @NotNull(message = "기술 스택 ID는 필수입니다.")
        @Positive(message = "기술 스택 ID는 양수여야 합니다.")
        Long techStackId,

        @NotNull(message = "기술 스택 표시 순서는 필수입니다.")
        @Positive(message = "기술 스택 표시 순서는 1 이상이어야 합니다.")
        Integer displayOrder
) {
}
