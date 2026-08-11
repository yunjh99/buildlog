package com.example.buildlog.education.dto;
import com.example.buildlog.education.domain.*;
import java.time.LocalDate;
public record EducationResponse(Long id, EducationType type, String institution, String program,
 LocalDate startDate, LocalDate endDate, String status, String description) {
 public static EducationResponse from(Education e){return new EducationResponse(e.getId(),e.getType(),e.getInstitution(),e.getProgram(),e.getStartDate(),e.getEndDate(),e.getStatus(),e.getDescription());}
}
