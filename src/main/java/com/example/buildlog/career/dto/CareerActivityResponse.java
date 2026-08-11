package com.example.buildlog.career.dto;

import com.example.buildlog.career.domain.CareerActivity;

public record CareerActivityResponse(Long id, String content) {
    public static CareerActivityResponse from(CareerActivity activity) {
        return new CareerActivityResponse(activity.getId(), activity.getContent());
    }
}
