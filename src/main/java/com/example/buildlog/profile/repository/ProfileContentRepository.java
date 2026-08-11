package com.example.buildlog.profile.repository;

import com.example.buildlog.profile.domain.ProfileContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileContentRepository extends JpaRepository<ProfileContent, Long> {
    Optional<ProfileContent> findFirstByOrderByIdAsc();
}
