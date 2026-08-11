package com.example.buildlog.techstack.repository;

import com.example.buildlog.techstack.domain.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {

    List<TechStack> findAllByOrderByNameAsc();

    Optional<TechStack> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
