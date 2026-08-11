package com.example.buildlog.education.service;
import com.example.buildlog.education.domain.Education;
import com.example.buildlog.education.dto.*;
import com.example.buildlog.education.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class EducationService {
 private final EducationRepository repository;
 public List<EducationResponse> findAll(){return repository.findAllByOrderByStartDateDescIdDesc().stream().map(EducationResponse::from).toList();}
 @Transactional public Long create(EducationRequest r){return repository.save(new Education(r.type(),r.institution().trim(),r.program().trim(),r.startDate(),r.endDate(),r.status(),r.description())).getId();}
 @Transactional public void update(Long id,EducationRequest r){find(id).update(r.type(),r.institution().trim(),r.program().trim(),r.startDate(),r.endDate(),r.status(),r.description());}
 @Transactional public void delete(Long id){repository.delete(find(id));}
 private Education find(Long id){return repository.findById(id).orElseThrow(()->new ResponseStatusException(NOT_FOUND,"존재하지 않는 교육입니다. id="+id));}
}
