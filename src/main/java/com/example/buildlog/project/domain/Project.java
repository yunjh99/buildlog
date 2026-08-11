package com.example.buildlog.project.domain;

import com.example.buildlog.techstack.domain.TechStack;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    // 진행 중인 프로젝트는 종료일을 비워 둔다.
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectType type;

    // 팀 프로젝트일 때만 2명 이상의 인원수를 저장한다.
    private Integer teamSize;

    // 프로젝트와 기술 스택의 연결 정보와 화면 표시 순서를 관리한다.
    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("displayOrder ASC")
    private List<ProjectTechStack> techStacks = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String githubUrl;

    @Column(length = 500)
    private String siteUrl;

    // 프로젝트에서 담당한 작업을 화면에 표시할 순서대로 관리한다.
    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("displayOrder ASC")
    private List<ProjectContribution> contributions = new ArrayList<>(); //주요 구현 내용 또는 역할

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Project(
            String name,
            LocalDate startDate,
            LocalDate endDate,
            ProjectType type,
            Integer teamSize,
            String description,
            String githubUrl,
            String siteUrl
    ) {
        validateTeamSize(type, teamSize);
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.teamSize = teamSize;
        this.description = description;
        this.githubUrl = githubUrl;
        this.siteUrl = siteUrl;
    }

    public void update(
            String name,
            LocalDate startDate,
            LocalDate endDate,
            ProjectType type,
            Integer teamSize,
            String description,
            String githubUrl,
            String siteUrl
    ) {
        validateTeamSize(type, teamSize);
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.teamSize = teamSize;
        this.description = description;
        this.githubUrl = githubUrl;
        this.siteUrl = siteUrl;
    }

    private void validateTeamSize(ProjectType type, Integer teamSize) {
        if (type == ProjectType.TEAM && (teamSize == null || teamSize < 2)) {
            throw new IllegalArgumentException("팀 프로젝트의 인원수는 2명 이상이어야 합니다.");
        }

        if (type != ProjectType.TEAM && teamSize != null) {
            throw new IllegalArgumentException("팀 프로젝트가 아닌 경우 인원수를 입력할 수 없습니다.");
        }
    }

    public ProjectTechStack addTechStack(
            TechStack techStack,
            Integer displayOrder
    ) {
        ProjectTechStack projectTechStack =
                new ProjectTechStack(this, techStack, displayOrder);
        this.techStacks.add(projectTechStack);
        return projectTechStack;
    }

    public void removeTechStack(ProjectTechStack projectTechStack) {
        this.techStacks.remove(projectTechStack);
    }

    public void clearTechStacks() {
        this.techStacks.clear();
    }

    public List<ProjectTechStack> getTechStacks() {
        return Collections.unmodifiableList(techStacks);
    }

    public ProjectContribution addContribution(
            String title,
            String detail,
            Integer displayOrder
    ) {
        ProjectContribution contribution =
                new ProjectContribution(this, title, detail, displayOrder);
        this.contributions.add(contribution);
        return contribution;
    }

    public void removeContribution(ProjectContribution contribution) {
        this.contributions.remove(contribution);
    }

    public void clearContributions() {
        this.contributions.clear();
    }

    public List<ProjectContribution> getContributions() {
        return Collections.unmodifiableList(contributions);
    }

    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
