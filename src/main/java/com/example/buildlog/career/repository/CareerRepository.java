package com.example.buildlog.career.repository;

import com.example.buildlog.career.domain.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {

    List<Career> findAllByOrderByStartDateDescIdDesc();
}
