package com.example.buildlog.project.repository;

import com.example.buildlog.project.domain.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Slice<Project> findAllByOrderByStartDateDescIdDesc(Pageable pageable);

    /**
     * 전달받은 기술 스택을 모두 포함하는 프로젝트를 조회한다.
     * 일치한 기술 스택 수가 선택한 기술 스택 수와 같은지 비교해 AND 조건을 만든다.
     */
    @Query(value = """
            select p
            from Project p
            where (
                select count(distinct lower(techStack.name))
                from ProjectTechStack projectTechStack
                join projectTechStack.techStack techStack
                where projectTechStack.project = p
                  and lower(techStack.name) in :techStackNames
            ) = :techStackCount
            order by p.startDate desc, p.id desc
            """)
    Slice<Project> findAllContainingTechStacks(
            @Param("techStackNames") Set<String> techStackNames,
            @Param("techStackCount") long techStackCount,
            Pageable pageable
    );
}
