package com.example.buildlog.project.dto;

import com.example.buildlog.project.domain.ProjectContribution;

public record ProjectContributionResponse(
        Long id,
        String title,
        String detail
) {
    public static ProjectContributionResponse from(ProjectContribution contribution) {
        return new ProjectContributionResponse(
                contribution.getId(),
                contribution.getTitle(),
                contribution.getDetail()
        );
    }
}
