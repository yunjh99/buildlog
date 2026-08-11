package com.example.buildlog.profile.dto;

import com.example.buildlog.profile.domain.ProfileContent;

public record ProfileContentResponse(
        String heroLine1, String heroLine2, String email, String githubUrl, String blogUrl,
        String aboutTitle, String aboutEmphasis, String aboutParagraph1, String aboutParagraph2
) {
    public static ProfileContentResponse from(ProfileContent content) {
        return new ProfileContentResponse(content.getHeroLine1(), content.getHeroLine2(), content.getEmail(),
                content.getGithubUrl(), content.getBlogUrl(), content.getAboutTitle(), content.getAboutEmphasis(),
                content.getAboutParagraph1(), content.getAboutParagraph2());
    }
}
