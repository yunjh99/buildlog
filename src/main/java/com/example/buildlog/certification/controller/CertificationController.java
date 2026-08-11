package com.example.buildlog.certification.controller;
import com.example.buildlog.certification.dto.*; import com.example.buildlog.certification.service.CertificationService; import com.example.buildlog.global.common.SuccessResponse;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/certifications") @RequiredArgsConstructor
public class CertificationController {private final CertificationService service;
 @GetMapping public SuccessResponse<List<CertificationResponse>> all(){return SuccessResponse.of(HttpStatus.OK,"자격증 목록 조회 성공",service.findAll());}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public SuccessResponse<Long> create(@Valid @RequestBody CertificationRequest r){return SuccessResponse.of(HttpStatus.CREATED,"자격증 생성 성공",service.create(r));}
 @PutMapping("/{id}") public SuccessResponse<Void> update(@PathVariable Long id,@Valid @RequestBody CertificationRequest r){service.update(id,r);return SuccessResponse.of(HttpStatus.OK,"자격증 수정 성공");}
 @DeleteMapping("/{id}") public SuccessResponse<Void> delete(@PathVariable Long id){service.delete(id);return SuccessResponse.of(HttpStatus.OK,"자격증 삭제 성공");}}
