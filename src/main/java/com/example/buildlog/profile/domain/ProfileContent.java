package com.example.buildlog.profile.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileContent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String heroLine1;

    @Column(nullable = false, length = 500)
    private String heroLine2;

    @Column(length = 200)
    private String email;

    @Column(length = 500)
    private String githubUrl;

    @Column(length = 500)
    private String blogUrl;

    @Column(nullable = false, length = 100)
    private String aboutTitle;

    @Column(nullable = false, length = 100)
    private String aboutEmphasis;

    @Column(nullable = false, length = 2000)
    private String aboutParagraph1;

    @Column(nullable = false, length = 2000)
    private String aboutParagraph2;

    public ProfileContent(String heroLine1, String heroLine2, String email, String githubUrl,
                          String blogUrl, String aboutTitle, String aboutEmphasis,
                          String aboutParagraph1, String aboutParagraph2) {
        update(heroLine1, heroLine2, email, githubUrl, blogUrl, aboutTitle, aboutEmphasis,
                aboutParagraph1, aboutParagraph2);
    }

    public void update(String heroLine1, String heroLine2, String email, String githubUrl,
                       String blogUrl, String aboutTitle, String aboutEmphasis,
                       String aboutParagraph1, String aboutParagraph2) {
        this.heroLine1 = heroLine1;
        this.heroLine2 = heroLine2;
        this.email = email;
        this.githubUrl = githubUrl;
        this.blogUrl = blogUrl;
        this.aboutTitle = aboutTitle;
        this.aboutEmphasis = aboutEmphasis;
        this.aboutParagraph1 = aboutParagraph1;
        this.aboutParagraph2 = aboutParagraph2;
    }
}
