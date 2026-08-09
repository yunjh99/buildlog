package com.example.buildlog.project.service;

import com.example.buildlog.project.domain.Project;
import com.example.buildlog.project.domain.ProjectType;
import com.example.buildlog.project.dto.ProjectContributionRequest;
import com.example.buildlog.project.dto.ProjectCreateRequest;
import com.example.buildlog.project.dto.ProjectTechStackRequest;
import com.example.buildlog.project.repository.ProjectRepository;
import com.example.buildlog.techstack.domain.TechStack;
import com.example.buildlog.techstack.repository.TechStackRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TechStackRepository techStackRepository;

    @Test
    @DisplayName("프로젝트와 기술 스택, 주요 작업을 함께 생성한다")
    void createProject() {
        // given: 프로젝트에서 선택할 기술 스택을 먼저 등록한다.
        TechStack java = techStackRepository.save(new TechStack("Java"));
        TechStack springBoot = techStackRepository.save(new TechStack("Spring Boot"));

        ProjectCreateRequest request = new ProjectCreateRequest(
                "BuildLog",
                LocalDate.of(2026, 8, 9),
                null,
                ProjectType.TEAM,
                3,
                "개발 프로젝트와 경력을 관리하는 서비스",
                List.of(
                        new ProjectTechStackRequest(java.getId(), 1),
                        new ProjectTechStackRequest(springBoot.getId(), 2)
                ),
                List.of(
                        new ProjectContributionRequest(
                                "프로젝트 생성 API 구현",
                                "프로젝트와 하위 데이터를 함께 저장",
                                1
                        ),
                        new ProjectContributionRequest(
                                "기술 스택 필터 구현",
                                null,
                                2
                        )
                )
        );

        // when
        Long projectId = projectService.create(request);

        // then
        Project savedProject = projectRepository.findById(projectId).orElseThrow();

        assertThat(savedProject.getName()).isEqualTo("BuildLog");
        assertThat(savedProject.getType()).isEqualTo(ProjectType.TEAM);
        assertThat(savedProject.getTeamSize()).isEqualTo(3);
        assertThat(savedProject.getTechStacks())
                .hasSize(2)
                .extracting(projectTechStack -> projectTechStack.getTechStack().getName())
                .containsExactly("Java", "Spring Boot");
        assertThat(savedProject.getContributions())
                .hasSize(2)
                .extracting(contribution -> contribution.getTitle())
                .containsExactly(
                        "프로젝트 생성 API 구현",
                        "기술 스택 필터 구현"
                );
    }

    @Test
    @DisplayName("존재하지 않는 기술 스택으로 프로젝트를 생성할 수 없다")
    void createProjectWithUnknownTechStack() {
        // given
        ProjectCreateRequest request = createRequest(
                ProjectType.PERSONAL,
                null,
                List.of(new ProjectTechStackRequest(999L, 1))
        );

        // when & then
        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 기술 스택입니다. id=999");
    }

    @Test
    @DisplayName("팀 프로젝트의 인원수가 2명 미만이면 생성할 수 없다")
    void createTeamProjectWithInvalidTeamSize() {
        // given
        ProjectCreateRequest request = createRequest(
                ProjectType.TEAM,
                1,
                List.of()
        );

        // when & then
        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("팀 프로젝트의 인원수는 2명 이상이어야 합니다.");
    }

    @Test
    @DisplayName("팀 프로젝트가 아니면 인원수를 입력할 수 없다")
    void createPersonalProjectWithTeamSize() {
        // given
        ProjectCreateRequest request = createRequest(
                ProjectType.PERSONAL,
                3,
                List.of()
        );

        // when & then
        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("팀 프로젝트가 아닌 경우 인원수를 입력할 수 없습니다.");
    }

    private ProjectCreateRequest createRequest(
            ProjectType type,
            Integer teamSize,
            List<ProjectTechStackRequest> techStacks
    ) {
        return new ProjectCreateRequest(
                "테스트 프로젝트",
                LocalDate.of(2026, 8, 9),
                null,
                type,
                teamSize,
                "테스트 설명",
                techStacks,
                List.of()
        );
    }
}
