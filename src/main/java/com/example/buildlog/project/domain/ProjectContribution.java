package com.example.buildlog.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_contributions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectContribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false)
    private Integer displayOrder;

    ProjectContribution(
            Project project,
            String title,
            String detail,
            Integer displayOrder
    ) {
        this.project = project;
        this.title = title;
        this.detail = detail;
        this.displayOrder = displayOrder;
    }

    public void update(String title, String detail, Integer displayOrder) {
        this.title = title;
        this.detail = detail;
        this.displayOrder = displayOrder;
    }
}
