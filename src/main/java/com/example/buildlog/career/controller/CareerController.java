package com.example.buildlog.career.controller;

import com.example.buildlog.career.dto.CareerCreateRequest;
import com.example.buildlog.career.dto.CareerResponse;
import com.example.buildlog.career.service.CareerService;
import com.example.buildlog.global.common.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/careers")
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse<Long> create(@Valid @RequestBody CareerCreateRequest request) {
        Long id = careerService.create(request);
        return SuccessResponse.of(HttpStatus.CREATED, "이력 생성 성공", id);
    }

    @PutMapping("/{id}")
    public SuccessResponse<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody CareerCreateRequest request
    ) {
        careerService.update(id, request);
        return SuccessResponse.of(HttpStatus.OK, "이력 수정 성공");
    }

    @DeleteMapping("/{id}")
    public SuccessResponse<Void> delete(@PathVariable Long id) {
        careerService.delete(id);
        return SuccessResponse.of(HttpStatus.OK, "이력 삭제 성공");
    }

    @GetMapping
    public SuccessResponse<List<CareerResponse>> findAll() {
        return SuccessResponse.of(HttpStatus.OK, "이력 목록 조회 성공", careerService.findAll());
    }
}
