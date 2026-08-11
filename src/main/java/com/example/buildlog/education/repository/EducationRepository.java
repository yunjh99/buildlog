package com.example.buildlog.education.repository;
import com.example.buildlog.education.domain.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EducationRepository extends JpaRepository<Education,Long>{List<Education> findAllByOrderByStartDateDescIdDesc();}
