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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "career_roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareerRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private Integer displayOrder;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<CareerActivity> activities = new ArrayList<>();

    CareerRole(Career career, String title, int displayOrder) {
        this.career = career;
        this.title = title;
        this.displayOrder = displayOrder;
    }

    public void addActivity(String content, int displayOrder) {
        activities.add(new CareerActivity(this, content, displayOrder));
    }

    public List<CareerActivity> getActivities() {
        return Collections.unmodifiableList(activities);
    }
}
