package com.example.buildlog.techstack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tech_stacks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TechStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 같은 기술명이 중복 등록되지 않도록 unique 제약 조건을 둔다.
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    public TechStack(String name) {
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
