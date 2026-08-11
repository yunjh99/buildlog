package com.example.buildlog.profile.service;

import com.example.buildlog.profile.domain.ProfileContent;
import com.example.buildlog.profile.dto.ProfileContentRequest;
import com.example.buildlog.profile.dto.ProfileContentResponse;
import com.example.buildlog.profile.repository.ProfileContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileContentService {

    private final ProfileContentRepository repository;

    public ProfileContentResponse find() {
        return repository.findFirstByOrderByIdAsc()
                .map(ProfileContentResponse::from)
                .orElseGet(ProfileContentService::defaults);
    }

    @Transactional
    public ProfileContentResponse update(ProfileContentRequest request) {
        ProfileContent content = repository.findFirstByOrderByIdAsc()
                .orElseGet(() -> new ProfileContent(
                        request.heroLine1(), request.heroLine2(), request.email(), request.githubUrl(),
                        request.blogUrl(), request.aboutTitle(), request.aboutEmphasis(),
                        request.aboutParagraph1(), request.aboutParagraph2()
                ));
        content.update(request.heroLine1(), request.heroLine2(), blankToNull(request.email()),
                blankToNull(request.githubUrl()), blankToNull(request.blogUrl()), request.aboutTitle(),
                request.aboutEmphasis(), request.aboutParagraph1(), request.aboutParagraph2());
        return ProfileContentResponse.from(repository.save(content));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static ProfileContentResponse defaults() {
        return new ProfileContentResponse(
                "좋은 서비스는 고객이 편하게 사용할 수 있어야 하고,",
                "좋은 코드는 다른 사람이 쉽게 읽고 이어갈 수 있어야 한다고 생각합니다.",
                null, null, null,
                "결과뿐 아니라", "과정을 남깁니다.",
                "B2B 웹 서비스 운영·유지보수를 담당하며 고객 문의를 바탕으로 오류를 재현하고, 데이터와 기존 소스 코드를 추적해 문제의 원인을 해결해 왔습니다. 업체별 상품 정보 처리 오류를 수정하고, 대량 상품 조회 시 발생하던 화면 지연을 렌더링 방식 개선으로 완화했습니다.",
                "운영 환경에서는 PostgreSQL과 JavaScript·PHP 기반의 기존 기능을 다루며 데이터 흐름과 비즈니스 로직을 이해했습니다. 프로젝트에서는 Java와 Spring Boot를 중심으로 REST API를 설계하고, JPA를 활용해 도메인과 연관관계를 모델링하며 안정적이고 유지보수하기 좋은 백엔드를 구현해 왔습니다."
        );
    }
}
