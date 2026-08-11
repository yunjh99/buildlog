package com.example.buildlog.education.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity @Table(name = "educations") @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private EducationType type;
    @Column(nullable = false, length = 150) private String institution;
    @Column(nullable = false, length = 150) private String program;
    @Column(nullable = false) private LocalDate startDate;
    private LocalDate endDate;
    @Column(length = 100) private String status;
    @Column(columnDefinition = "TEXT") private String description;

    public Education(EducationType type, String institution, String program, LocalDate startDate,
                     LocalDate endDate, String status, String description) {
        update(type, institution, program, startDate, endDate, status, description);
    }
    public void update(EducationType type, String institution, String program, LocalDate startDate,
                       LocalDate endDate, String status, String description) {
        this.type=type; this.institution=institution; this.program=program; this.startDate=startDate;
        this.endDate=endDate; this.status=status; this.description=description;
    }
}
