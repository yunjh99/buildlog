package com.example.buildlog.career.dto;

import com.example.buildlog.career.domain.CareerSection;

import java.util.List;

public record CareerSectionResponse(Long id, String title, List<CareerActivityResponse> activities) {
    public static CareerSectionResponse from(CareerSection section) {
        return new CareerSectionResponse(
                section.getId(), section.getTitle(),
                section.getActivities().stream().map(CareerActivityResponse::from).toList()
        );
    }
}
