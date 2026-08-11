package com.example.buildlog.project.controller;

import com.example.buildlog.global.common.SuccessResponse;
import com.example.buildlog.global.security.Login;
import com.example.buildlog.project.dto.ProjectCreateRequest;
import com.example.buildlog.project.dto.ProjectSliceResponse;
import com.example.buildlog.project.service.ProjectService;
import com.example.buildlog.user.dto.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 프로젝트를 생성하고 성공 응답을 반환한다.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> create(
            @Valid @RequestBody ProjectCreateRequest request,
            @Login LoginUser loginUser
    ) {
        projectService.create(request, loginUser);

        return SuccessResponse.of(HttpStatus.CREATED, "프로젝트 생성 성공");
    }

    @PutMapping("/{id}")
    public SuccessResponse<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectCreateRequest request
    ) {
        projectService.update(id, request);
        return SuccessResponse.of(HttpStatus.OK, "프로젝트 수정 성공");
    }

    @DeleteMapping("/{id}")
    public SuccessResponse<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return SuccessResponse.of(HttpStatus.OK, "프로젝트 삭제 성공");
    }

    /**
     * 프로젝트 목록을 조회한다. techStack을 여러 번 전달하면 모든 기술을 포함한 프로젝트만 조회한다.
    */
    @GetMapping
    public SuccessResponse<ProjectSliceResponse> findAll(
            @RequestParam(required = false) List<String> techStack,
            @PageableDefault(size = 12) Pageable pageable
    ) {
        ProjectSliceResponse projects = projectService.findAll(techStack, pageable);

        return SuccessResponse.of(HttpStatus.OK, "프로젝트 목록 조회 성공", projects);
    }
}
