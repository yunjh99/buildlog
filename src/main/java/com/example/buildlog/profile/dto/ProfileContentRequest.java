package com.example.buildlog.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileContentRequest(
        @NotBlank @Size(max = 500) String heroLine1,
        @NotBlank @Size(max = 500) String heroLine2,
        @Size(max = 200) String email,
        @Size(max = 500) String githubUrl,
        @Size(max = 500) String blogUrl,
        @NotBlank @Size(max = 100) String aboutTitle,
        @NotBlank @Size(max = 100) String aboutEmphasis,
        @NotBlank @Size(max = 2000) String aboutParagraph1,
        @Size(max = 2000) String aboutParagraph2
) {}
