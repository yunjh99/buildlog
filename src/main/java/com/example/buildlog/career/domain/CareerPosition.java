package com.example.buildlog.career.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "career_positions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareerPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;

    @Column(length = 100)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private Integer displayOrder;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<CareerSection> sections = new ArrayList<>();

    CareerPosition(Career career, String title, LocalDate startDate, LocalDate endDate, int displayOrder) {
        this.career = career;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.displayOrder = displayOrder;
    }

    public CareerSection addSection(String title, int displayOrder) {
        CareerSection section = new CareerSection(this, title, displayOrder);
        sections.add(section);
        return section;
    }

    public void clearSections() {
        sections.clear();
    }

    public List<CareerSection> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
