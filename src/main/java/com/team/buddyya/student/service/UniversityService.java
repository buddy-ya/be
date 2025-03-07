package com.team.buddyya.student.service;

import com.team.buddyya.student.dto.response.UniversityResponse;
import com.team.buddyya.student.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UniversityService {

    private final UniversityRepository universityRepository;

    @Transactional(readOnly = true)
    public List<UniversityResponse> getActiveUniversities() {
        return universityRepository.findByIsActiveTrue().stream()
                .map(UniversityResponse::from)
                .toList();
    }
}
