package com.example.buildlog.career.service;

import com.example.buildlog.career.domain.Career;
import com.example.buildlog.career.domain.CareerRole;
import com.example.buildlog.career.dto.CareerActivityRequest;
import com.example.buildlog.career.dto.CareerCreateRequest;
import com.example.buildlog.career.dto.CareerResponse;
import com.example.buildlog.career.dto.CareerRoleRequest;
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

        addRoles(career, request);
        return careerRepository.save(career).getId();
    }

    @Transactional
    public void update(Long id, CareerCreateRequest request) {
        Career career = findById(id);
        career.update(request.companyName().trim(), request.startDate(), request.endDate());
        career.clearRoles();
        addRoles(career, request);
    }

    @Transactional
    public void delete(Long id) {
        careerRepository.delete(findById(id));
    }

    private void addRoles(Career career, CareerCreateRequest request) {
        for (int roleIndex = 0; roleIndex < request.roles().size(); roleIndex++) {
            CareerRoleRequest roleRequest = request.roles().get(roleIndex);
            CareerRole role = career.addRole(roleRequest.title().trim(), roleIndex + 1);

            for (int activityIndex = 0; activityIndex < roleRequest.activities().size(); activityIndex++) {
                CareerActivityRequest activity = roleRequest.activities().get(activityIndex);
                role.addActivity(activity.content().trim(), activityIndex + 1);
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
