package com.example.buildlog.techstack.service;

import com.example.buildlog.project.repository.ProjectTechStackRepository;
import com.example.buildlog.techstack.domain.TechStack;
import com.example.buildlog.techstack.dto.TechStackCreateRequest;
import com.example.buildlog.techstack.dto.TechStackResponse;
import com.example.buildlog.techstack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TechStackService {

    private final TechStackRepository techStackRepository;
    private final ProjectTechStackRepository projectTechStackRepository;

    @Transactional
    public Long create(TechStackCreateRequest request) {
        String name = request.name().trim();

        if (techStackRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("이미 등록된 기술 스택입니다: " + name);
        }

        return techStackRepository.save(new TechStack(name, request.category())).getId();
    }

    @Transactional
    public void delete(Long id) {
        TechStack techStack = techStackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "존재하지 않는 기술 스택입니다. id=" + id
                ));

        if (projectTechStackRepository.existsByTechStack_Id(id)) {
            throw new ResponseStatusException(
                    CONFLICT,
                    "프로젝트에서 사용 중인 기술 스택은 삭제할 수 없습니다."
            );
        }

        techStackRepository.delete(techStack);
    }

    public List<TechStackResponse> findAll() {
        return techStackRepository.findAllByOrderByNameAsc().stream()
                .map(TechStackResponse::from)
                .toList();
    }
}
