package com.example.buildlog.certification.repository;
import com.example.buildlog.certification.domain.Certification; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface CertificationRepository extends JpaRepository<Certification,Long>{List<Certification> findAllByOrderByAcquiredDateDescIdDesc();}
