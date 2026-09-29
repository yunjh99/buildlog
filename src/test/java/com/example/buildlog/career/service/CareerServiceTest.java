package com.example.buildlog.career.service;

import com.example.buildlog.career.dto.CareerActivityRequest;
import com.example.buildlog.career.dto.CareerCreateRequest;
import com.example.buildlog.career.dto.CareerPositionRequest;
import com.example.buildlog.career.dto.CareerResponse;
import com.example.buildlog.career.dto.CareerSectionRequest;
import com.example.buildlog.career.repository.CareerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CareerServiceTest {

    @Autowired CareerService careerService;
    @Autowired CareerRepository careerRepository;
    @Autowired EntityManager entityManager;

    @Test
    void storesMultiplePositionsAndNestedSectionsInOrder() {
        LocalDate firstDay = LocalDate.of(2025, 7, 1);
        LocalDate changeDay = LocalDate.of(2026, 1, 1);
        LocalDate lastDay = LocalDate.of(2026, 3, 31);
        Long id = careerService.create(new CareerCreateRequest(
                "블루시스", firstDay, lastDay,
                List.of(
                        new CareerPositionRequest("플랫폼 운영", firstDay, LocalDate.of(2025, 12, 31), List.of(
                                section("주요 담당업무", "고객 데이터 관리"),
                                section("시스템 개선", "단가 이월 오류 수정")
                        )),
                        new CareerPositionRequest("백엔드 개발", changeDay, lastDay, List.of(
                                section("개발", "상품 API 구현")
                        ))
                )
        ));

        entityManager.flush();
        entityManager.clear();

        CareerResponse result = careerService.findAll().stream()
                .filter(career -> career.id().equals(id)).findFirst().orElseThrow();
        assertThat(result.positions()).extracting(position -> position.title())
                .containsExactly("플랫폼 운영", "백엔드 개발");
        assertThat(result.positions().get(0).sections()).extracting(section -> section.title())
                .containsExactly("주요 담당업무", "시스템 개선");
        assertThat(result.positions().get(0).sections().get(1).activities().get(0).content())
                .isEqualTo("단가 이월 오류 수정");

        careerService.delete(id);
        entityManager.flush();
        entityManager.clear();
        assertThat(careerRepository.findById(id)).isEmpty();
        assertThat(entityManager.createQuery("select count(p) from CareerPosition p", Long.class).getSingleResult()).isZero();
        assertThat(entityManager.createQuery("select count(s) from CareerSection s", Long.class).getSingleResult()).isZero();
        assertThat(entityManager.createQuery("select count(a) from CareerActivity a", Long.class).getSingleResult()).isZero();
    }

    private CareerSectionRequest section(String title, String content) {
        return new CareerSectionRequest(title, List.of(new CareerActivityRequest(content)));
    }
}
