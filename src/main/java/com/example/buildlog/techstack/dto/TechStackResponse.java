package com.example.buildlog.techstack.dto;

import com.example.buildlog.techstack.domain.TechStack;
import com.example.buildlog.techstack.domain.TechStackCategory;

public record TechStackResponse(
        Long id,
        String name,
        TechStackCategory category
) {
    public static TechStackResponse from(TechStack techStack) {
        TechStackCategory category = techStack.getCategory() != null
                ? techStack.getCategory()
                : TechStackCategory.UNCATEGORIZED;
        return new TechStackResponse(techStack.getId(), techStack.getName(), category);
    }
}
