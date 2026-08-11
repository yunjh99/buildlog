package com.example.buildlog.techstack.dto;

import com.example.buildlog.techstack.domain.TechStackCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TechStackCreateRequest(
        @NotBlank(message = "기술명은 필수입니다.")
        @Size(max = 50, message = "기술명은 50자 이하여야 합니다.")
        String name,

        @NotNull(message = "기술 스택 카테고리는 필수입니다.")
        TechStackCategory category
) {
}
