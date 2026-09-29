package com.example.buildlog.career.service;

import com.example.buildlog.career.domain.Career;
import com.example.buildlog.career.domain.CareerPosition;
import com.example.buildlog.career.domain.CareerSection;
import com.example.buildlog.career.dto.CareerActivityRequest;
import com.example.buildlog.career.dto.CareerCreateRequest;
import com.example.buildlog.career.dto.CareerPositionRequest;
import com.example.buildlog.career.dto.CareerResponse;
import com.example.buildlog.career.dto.CareerSectionRequest;
import com.example.buildlog.career.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerService {

    private final CareerRepository careerRepository;

    @Transactional
    public Long create(CareerCreateRequest request) {
        Career career = new Career(
                request.companyName().trim(),
                request.startDate(),
                request.endDate()
        );

        addPositions(career, request);
        return careerRepository.save(career).getId();
    }

    @Transactional
    public void update(Long id, CareerCreateRequest request) {
        Career career = findById(id);
        career.update(request.companyName().trim(), request.startDate(), request.endDate());
        career.clearPositions();
        addPositions(career, request);
    }

    @Transactional
    public void delete(Long id) {
        careerRepository.delete(findById(id));
    }

    private void addPositions(Career career, CareerCreateRequest request) {
        for (int positionIndex = 0; positionIndex < request.positions().size(); positionIndex++) {
            CareerPositionRequest positionRequest = request.positions().get(positionIndex);
            String title = positionRequest.title() == null || positionRequest.title().isBlank()
                    ? null : positionRequest.title().trim();
            CareerPosition position = career.addPosition(
                    title, positionRequest.startDate(), positionRequest.endDate(), positionIndex + 1
            );

            for (int sectionIndex = 0; sectionIndex < positionRequest.sections().size(); sectionIndex++) {
                CareerSectionRequest sectionRequest = positionRequest.sections().get(sectionIndex);
                CareerSection section = position.addSection(sectionRequest.title().trim(), sectionIndex + 1);
                for (int activityIndex = 0; activityIndex < sectionRequest.activities().size(); activityIndex++) {
                    CareerActivityRequest activity = sectionRequest.activities().get(activityIndex);
                    section.addActivity(activity.content().trim(), activityIndex + 1);
                }
            }
        }
    }

    private Career findById(Long id) {
        return careerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "존재하지 않는 이력입니다. id=" + id
                ));
    }

    public List<CareerResponse> findAll() {
        return careerRepository.findAllByOrderByStartDateDescIdDesc().stream()
                .map(CareerResponse::from)
                .toList();
    }
}
