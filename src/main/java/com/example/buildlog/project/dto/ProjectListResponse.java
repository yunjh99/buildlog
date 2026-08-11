package com.example.buildlog.project.dto;

import com.example.buildlog.project.domain.Project;
import com.example.buildlog.project.domain.ProjectType;
import com.example.buildlog.techstack.dto.TechStackResponse;

import java.time.LocalDate;
import java.util.List;

public record ProjectListResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        ProjectType type,
        Integer teamSize,
        String description,
        String githubUrl,
        String siteUrl,
        List<TechStackResponse> techStacks,
        List<ProjectContributionResponse> contributions
) {

    public static ProjectListResponse from(Project project) {
        List<TechStackResponse> techStackResponses = project.getTechStacks().stream()
                .map(projectTechStack -> TechStackResponse.from(projectTechStack.getTechStack()))
                .toList();

        List<ProjectContributionResponse> contributions = project.getContributions().stream()
                .map(ProjectContributionResponse::from)
                .toList();

        return new ProjectListResponse(
                project.getId(),
                project.getName(),
                project.getStartDate(),
                project.getEndDate(),
                project.getType(),
                project.getTeamSize(),
                project.getDescription(),
                project.getGithubUrl(),
                project.getSiteUrl(),
                techStackResponses,
                contributions
        );
    }
}
