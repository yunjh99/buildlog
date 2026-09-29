package com.example.buildlog.career.dto;

import com.example.buildlog.career.domain.Career;

import java.time.LocalDate;
import java.util.List;

public record CareerResponse(
        Long id,
        String companyName,
        LocalDate startDate,
        LocalDate endDate,
        List<CareerPositionResponse> positions
) {
    public static CareerResponse from(Career career) {
        return new CareerResponse(
                career.getId(),
                career.getCompanyName(),
                career.getStartDate(),
                career.getEndDate(),
                career.getPositions().stream().map(CareerPositionResponse::from).toList()
        );
    }
}
