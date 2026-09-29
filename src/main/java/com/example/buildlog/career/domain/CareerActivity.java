package com.example.buildlog.career.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "career_activities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareerActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_role_id", nullable = false) // DB 컬럼은 그대로
    private CareerSection section;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private Integer displayOrder;

    CareerActivity(CareerSection section, String content, int displayOrder) {
        this.section = section;
        this.content = content;
        this.displayOrder = displayOrder;
    }
}
