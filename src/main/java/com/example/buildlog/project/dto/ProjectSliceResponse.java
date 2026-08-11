package com.example.buildlog.project.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record ProjectSliceResponse(
        List<ProjectListResponse> projects,
        boolean hasNext
) {
    public static ProjectSliceResponse from(Slice<ProjectListResponse> slice) {
        return new ProjectSliceResponse(slice.getContent(), slice.hasNext());
    }
}
