package com.example.buildlog.project.dto;

import com.example.buildlog.project.domain.ProjectType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record ProjectCreateRequest(
        @NotBlank(message = "프로젝트명은 필수입니다.")
        @Size(max = 100, message = "프로젝트명은 100자 이하여야 합니다.")
        String name,

        @NotNull(message = "프로젝트 시작일은 필수입니다.")
        LocalDate startDate,

        // 진행 중인 프로젝트는 종료일을 전달하지 않는다.
        LocalDate endDate,

        @NotNull(message = "프로젝트 유형은 필수입니다.")
        ProjectType type,

        // TEAM일 때만 사용하며 최소 2명이어야 한다.
        @Min(value = 2, message = "팀 프로젝트의 인원수는 2명 이상이어야 합니다.")
        Integer teamSize,

        @Size(max = 1000, message = "프로젝트 설명은 1,000자 이하여야 합니다.")
        String description,

        @NotNull(message = "기술 스택 목록은 필수입니다.")
        List<@Valid ProjectTechStackRequest> techStacks,

        @NotNull(message = "주요 작업 목록은 필수입니다.")
        List<@Valid ProjectContributionRequest> contributions
) {

    @AssertTrue(message = "프로젝트 종료일은 시작일보다 빠를 수 없습니다.")
    public boolean isValidPeriod() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }

    @AssertTrue(message = "팀 프로젝트만 2명 이상의 인원수를 입력할 수 있습니다.")
    public boolean isValidTeamSize() {
        if (type == null) {
            return true;
        }

        return type == ProjectType.TEAM
                ? teamSize != null && teamSize >= 2
                : teamSize == null;
    }
}
