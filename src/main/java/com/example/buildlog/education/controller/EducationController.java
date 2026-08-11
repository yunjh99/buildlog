package com.example.buildlog.education.controller;
import com.example.buildlog.education.dto.*; import com.example.buildlog.education.service.EducationService; import com.example.buildlog.global.common.SuccessResponse;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/educations") @RequiredArgsConstructor
public class EducationController { private final EducationService service;
 @GetMapping public SuccessResponse<List<EducationResponse>> all(){return SuccessResponse.of(HttpStatus.OK,"교육 목록 조회 성공",service.findAll());}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public SuccessResponse<Long> create(@Valid @RequestBody EducationRequest r){return SuccessResponse.of(HttpStatus.CREATED,"교육 생성 성공",service.create(r));}
 @PutMapping("/{id}") public SuccessResponse<Void> update(@PathVariable Long id,@Valid @RequestBody EducationRequest r){service.update(id,r);return SuccessResponse.of(HttpStatus.OK,"교육 수정 성공");}
 @DeleteMapping("/{id}") public SuccessResponse<Void> delete(@PathVariable Long id){service.delete(id);return SuccessResponse.of(HttpStatus.OK,"교육 삭제 성공");}
}
