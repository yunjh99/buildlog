package com.example.buildlog.career.dto;

import com.example.buildlog.career.domain.CareerPosition;

import java.time.LocalDate;
import java.util.List;

public record CareerPositionResponse(
        Long id, String title, LocalDate startDate, LocalDate endDate,
        List<CareerSectionResponse> sections
) {
    public static CareerPositionResponse from(CareerPosition position) {
        return new CareerPositionResponse(
                position.getId(), position.getTitle(), position.getStartDate(), position.getEndDate(),
                position.getSections().stream().map(CareerSectionResponse::from).toList()
        );
    }
}
