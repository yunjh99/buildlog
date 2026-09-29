package com.example.buildlog.career.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CareerCreateRequest(
        @NotBlank(message = "회사명은 필수입니다.")
        @Size(max = 100, message = "회사명은 100자 이하여야 합니다.")
        String companyName,

        @NotNull(message = "재직 시작일은 필수입니다.")
        LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "직무 목록은 필수입니다.")
        @Size(min = 1, message = "직무를 하나 이상 입력해야 합니다.")
        List<@NotNull @Valid CareerPositionRequest> positions
) {
    @AssertTrue(message = "퇴사일은 입사일보다 빠를 수 없습니다.")
    public boolean isValidPeriod() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }

    @AssertTrue(message = "직무 기간은 회사 재직 기간 안에 있어야 합니다.")
    public boolean isValidPositionPeriods() {
        if (startDate == null || positions == null) return true;
        for (CareerPositionRequest position : positions) {
            if (position == null || position.startDate() == null) continue;
            if (position.startDate().isBefore(startDate)) return false;
            if (endDate != null && position.startDate().isAfter(endDate)) return false;
            if (endDate != null && position.endDate() == null) return false;
            if (position.endDate() != null && endDate != null && position.endDate().isAfter(endDate)) return false;
        }
        return true;
    }
}
