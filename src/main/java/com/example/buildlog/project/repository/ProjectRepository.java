package com.example.buildlog.project.repository;

import com.example.buildlog.project.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(
            value = """
                    select distinct p
                    from Project p
                    join p.techStacks projectTechStack
                    join projectTechStack.techStack techStack
                    where lower(techStack.name) = lower(:techStackName)
                    """,
            countQuery = """
                    select count(distinct p.id)
                    from Project p
                    join p.techStacks projectTechStack
                    join projectTechStack.techStack techStack
                    where lower(techStack.name) = lower(:techStackName)
                    """
    )
    Page<Project> findAllByTechStackName(
            @Param("techStackName") String techStackName,
            Pageable pageable
    );
}
