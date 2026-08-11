package com.example.buildlog.project.service;

import com.example.buildlog.global.error.exception.UserNotFoundException;
import com.example.buildlog.project.domain.Project;
import com.example.buildlog.project.dto.ProjectContributionRequest;
import com.example.buildlog.project.dto.ProjectCreateRequest;
import com.example.buildlog.project.dto.ProjectListResponse;
import com.example.buildlog.project.dto.ProjectSliceResponse;
import com.example.buildlog.project.dto.ProjectTechStackRequest;
import com.example.buildlog.project.repository.ProjectRepository;
import com.example.buildlog.techstack.domain.TechStack;
import com.example.buildlog.techstack.repository.TechStackRepository;
import com.example.buildlog.user.domain.User;
import com.example.buildlog.user.dto.LoginUser;
import com.example.buildlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TechStackRepository techStackRepository;
    private final UserRepository userRepository;

    /**
     * 프로젝트와 프로젝트에 속한 기술 스택, 주요 작업을 함께 생성한다.
     *
     * @return 생성된 프로젝트 ID
     */
    @Transactional
    public Long create(ProjectCreateRequest request, LoginUser loginUser) {
        Project project = new Project(
                request.name(),
                request.startDate(),
                request.endDate(),
                request.type(),
                request.teamSize(),
                request.description(),
                request.githubUrl(),
                request.siteUrl()
        );

        // 로그인된 사용자 정보를 기반으로 사용자 조회
        User user = userRepository.findByLoginIdAndRole(loginUser.getLoginId(), loginUser.getRole())
                .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

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

    @Transactional
    public void update(Long id, ProjectCreateRequest request) {
        Project project = findById(id);
        project.update(
                request.name().trim(),
                request.startDate(),
                request.endDate(),
                request.type(),
                request.teamSize(),
                request.description(),
                request.githubUrl(),
                request.siteUrl()
        );
        project.clearTechStacks();
        project.clearContributions();
        projectRepository.flush();

        for (ProjectTechStackRequest item : request.techStacks()) {
            TechStack techStack = techStackRepository.findById(item.techStackId())
                    .orElseThrow(() -> new ResponseStatusException(
                            NOT_FOUND, "존재하지 않는 기술 스택입니다. id=" + item.techStackId()
                    ));
            project.addTechStack(techStack, item.displayOrder());
        }

        for (ProjectContributionRequest item : request.contributions()) {
            project.addContribution(item.title(), item.detail(), item.displayOrder());
        }
    }

    @Transactional
    public void delete(Long id) {
        projectRepository.delete(findById(id));
    }

    private Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "존재하지 않는 프로젝트입니다. id=" + id
                ));
    }

    /**
     * 기술 스택 필터가 없으면 전체 프로젝트를, 있으면 선택한 기술을 모두 포함한 프로젝트를 조회한다.
     */
    public ProjectSliceResponse findAll(
            Collection<String> techStacks,
            Pageable pageable
    ) {
        Set<String> normalizedTechStacks = normalizeTechStacks(techStacks);

        Slice<Project> projects = normalizedTechStacks.isEmpty()
                ? projectRepository.findAllByOrderByStartDateDescIdDesc(pageable)
                : projectRepository.findAllContainingTechStacks(
                        normalizedTechStacks,
                        normalizedTechStacks.size(),
                        pageable
                );

        return ProjectSliceResponse.from(projects.map(ProjectListResponse::from));
    }

    private Set<String> normalizeTechStacks(Collection<String> techStacks) {
        Set<String> normalized = new LinkedHashSet<>();

        if (techStacks == null) {
            return normalized;
        }

        for (String techStack : techStacks) {
            if (techStack != null && !techStack.isBlank()) {
                normalized.add(techStack.trim().toLowerCase(Locale.ROOT));
            }
        }

        return normalized;
    }
}
