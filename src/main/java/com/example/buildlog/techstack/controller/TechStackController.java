package com.example.buildlog.techstack.controller;

import com.example.buildlog.global.common.SuccessResponse;
import com.example.buildlog.techstack.dto.TechStackCreateRequest;
import com.example.buildlog.techstack.dto.TechStackResponse;
import com.example.buildlog.techstack.service.TechStackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tech-stacks")
@RequiredArgsConstructor
public class TechStackController {

    private final TechStackService techStackService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Void> create(
            @Valid @RequestBody TechStackCreateRequest request
            ){
        techStackService.create(request);

        return SuccessResponse.of(HttpStatus.CREATED, "기술스택 생성 성공");
    }

    @DeleteMapping("/{id}")
    public SuccessResponse<Void> delete(@PathVariable Long id) {
        techStackService.delete(id);

        return SuccessResponse.of(HttpStatus.OK, "기술스택 삭제 성공");
    }

    @GetMapping
    public SuccessResponse<List<TechStackResponse>> findAll() {
        return SuccessResponse.of(
                HttpStatus.OK,
                "기술스택 목록 조회 성공",
                techStackService.findAll()
        );
    }
}
