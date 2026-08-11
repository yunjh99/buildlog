package com.example.buildlog.project.repository;

import com.example.buildlog.project.domain.ProjectTechStack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTechStackRepository extends JpaRepository<ProjectTechStack, Long> {

    boolean existsByTechStack_Id(Long techStackId);
}
