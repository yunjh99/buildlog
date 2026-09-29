package com.example.buildlog.career.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CareerPositionRequest(
        @Size(max = 100, message = "직무명은 100자 이하여야 합니다.")
        String title,
        @NotNull(message = "직무 시작일은 필수입니다.")
        LocalDate startDate,
        LocalDate endDate,
        @NotNull(message = "섹션 목록은 필수입니다.")
        @Size(min = 1, message = "직무마다 섹션을 하나 이상 입력해야 합니다.")
        List<@NotNull @Valid CareerSectionRequest> sections
) {
    @AssertTrue(message = "직무 종료일은 시작일보다 빠를 수 없습니다.")
    public boolean isValidPeriod() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
