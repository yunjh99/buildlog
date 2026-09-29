package com.example.buildlog.career.dto;

import com.example.buildlog.career.domain.CareerSection;

import java.util.List;

public record CareerRoleResponse(Long id, String title, List<CareerActivityResponse> activities) {
    public static CareerRoleResponse from(CareerSection role) {
        return new CareerRoleResponse(
                role.getId(),
                role.getTitle(),
                role.getActivities().stream().map(CareerActivityResponse::from).toList()
        );
    }
}
