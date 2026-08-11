package com.example.buildlog.techstack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    // 기존 데이터와의 호환을 위해 컬럼 추가 시에는 null을 허용하고 응답에서 미분류로 처리한다.
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TechStackCategory category;

    public TechStack(String name) {
        this.name = name;
        this.category = TechStackCategory.UNCATEGORIZED;
    }

    public TechStack(String name, TechStackCategory category) {
        this.name = name;
        this.category = category;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void update(String name, TechStackCategory category) {
        this.name = name;
        this.category = category;
    }
}
