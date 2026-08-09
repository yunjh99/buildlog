package com.example.buildlog.project.service;

import com.example.buildlog.project.domain.Project;
import com.example.buildlog.project.dto.ProjectContributionRequest;
import com.example.buildlog.project.dto.ProjectCreateRequest;
import com.example.buildlog.project.dto.ProjectTechStackRequest;
import com.example.buildlog.project.repository.ProjectRepository;
import com.example.buildlog.techstack.domain.TechStack;
import com.example.buildlog.techstack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TechStackRepository techStackRepository;

    /**
     * 프로젝트와 프로젝트에 속한 기술 스택, 주요 작업을 함께 생성한다.
     *
     * @return 생성된 프로젝트 ID
     */
    @Transactional
    public Long create(ProjectCreateRequest request) {
        Project project = new Project(
                request.name(),
                request.startDate(),
                request.endDate(),
                request.type(),
                request.teamSize(),
                request.description()
        );

        // 요청받은 기술 스택 ID를 실제 TechStack 엔티티로 조회해 프로젝트에 연결한다.
        for (ProjectTechStackRequest techStackRequest : request.techStacks()) {
            TechStack techStack = techStackRepository
                    .findById(techStackRequest.techStackId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "존재하지 않는 기술 스택입니다. id="
                                    + techStackRequest.techStackId()
                    ));

            project.addTechStack(
                    techStack,
                    techStackRequest.displayOrder()
            );
        }

        // 주요 작업은 Project가 소유하므로 Project를 통해 생성한다.
        for (ProjectContributionRequest contribution : request.contributions()) {
            project.addContribution(
                    contribution.title(),
                    contribution.detail(),
                    contribution.displayOrder()
            );
        }

        // cascade 설정으로 연결된 기술 스택과 주요 작업도 함께 저장된다.
        return projectRepository.save(project).getId();
    }
}
