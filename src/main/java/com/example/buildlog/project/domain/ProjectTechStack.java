package com.example.buildlog.project.domain;

import com.example.buildlog.techstack.domain.TechStack;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "project_tech_stacks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_project_tech_stack",
                columnNames = {"project_id", "tech_stack_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectTechStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_stack_id", nullable = false)
    private TechStack techStack;

    @Column(nullable = false)
    private Integer displayOrder;


    ProjectTechStack(
            Project project,
            TechStack techStack,
            Integer displayOrder
    ) {
        this.project = project;
        this.techStack = techStack;
        this.displayOrder = displayOrder;
    }

    public void update(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
