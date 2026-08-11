package com.example.buildlog.education.dto;
import com.example.buildlog.education.domain.EducationType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EducationRequest(
        @NotNull EducationType type, @NotBlank @Size(max=150) String institution,
        @NotBlank @Size(max=150) String program, @NotNull LocalDate startDate, LocalDate endDate,
        @Size(max=100) String status, @Size(max=3000) String description
) {
    @AssertTrue(message="종료일은 시작일보다 빠를 수 없습니다.")
    public boolean isValidPeriod(){ return startDate==null||endDate==null||!endDate.isBefore(startDate); }
}
