package com.example.buildlog.career.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "careers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @OneToMany(mappedBy = "career", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<CareerRole> roles = new ArrayList<>();

    public Career(String companyName, LocalDate startDate, LocalDate endDate) {
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void update(String companyName, LocalDate startDate, LocalDate endDate) {
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void clearRoles() {
        roles.clear();
    }

    public CareerRole addRole(String title, int displayOrder) {
        CareerRole role = new CareerRole(this, title, displayOrder);
        roles.add(role);
        return role;
    }

    public List<CareerRole> getRoles() {
        return Collections.unmodifiableList(roles);
    }
}
