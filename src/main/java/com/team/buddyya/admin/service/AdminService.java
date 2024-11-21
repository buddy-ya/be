package com.team.buddyya.admin.service;

import com.team.buddyya.admin.dto.response.StudentIdCardResponse;
import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final StudentIdCardRepository studentIdCardRepository;

    public List<StudentIdCardResponse> getStudentIdCards() {
        List<StudentIdCard> studentIdCards = studentIdCardRepository.findAllByOrderByCreatedDateAsc();
        List<StudentIdCardResponse> studentIdCardResponses = new ArrayList<>();
        for(StudentIdCard studentIdCard : studentIdCards) {
            studentIdCardResponses.add(StudentIdCardResponse.from(studentIdCard));
        }
        return studentIdCardResponses;
    }
}
