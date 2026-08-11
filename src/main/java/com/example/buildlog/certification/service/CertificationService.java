package com.example.buildlog.certification.service;
import com.example.buildlog.certification.domain.Certification; import com.example.buildlog.certification.dto.*; import com.example.buildlog.certification.repository.CertificationRepository;
import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.server.ResponseStatusException;
import java.util.List; import static org.springframework.http.HttpStatus.NOT_FOUND;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class CertificationService {private final CertificationRepository repository;
 public List<CertificationResponse> findAll(){return repository.findAllByOrderByAcquiredDateDescIdDesc().stream().map(CertificationResponse::from).toList();}
 @Transactional public Long create(CertificationRequest r){return repository.save(new Certification(r.name().trim(),r.issuer().trim(),r.acquiredDate(),r.credentialId(),r.credentialUrl())).getId();}
 @Transactional public void update(Long id,CertificationRequest r){find(id).update(r.name().trim(),r.issuer().trim(),r.acquiredDate(),r.credentialId(),r.credentialUrl());}
 @Transactional public void delete(Long id){repository.delete(find(id));}
 private Certification find(Long id){return repository.findById(id).orElseThrow(()->new ResponseStatusException(NOT_FOUND,"존재하지 않는 자격증입니다. id="+id));}}
