package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.Avatar;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.repository.AvatarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AvatarService {

    private final AvatarRepository avatarRepository;

    public void createAvatar(OnBoardingRequest request, Student student) {
        Avatar avatar = Avatar.builder()
                .isNotificationEnabled(request.isNotificationEnabled())
                .student(student)
                .build();
        avatar.setStudent(student);
        avatarRepository.save(avatar);
    }
}
