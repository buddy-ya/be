package com.team.buddyya.match.service;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.service.ChatRequestService;
import com.team.buddyya.chatting.service.ChatService;
import com.team.buddyya.match.domain.Buddy;
import com.team.buddyya.match.domain.MatchRequest;
import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.repositorry.BuddyRepository;
import com.team.buddyya.match.repositorry.MatchRepository;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final BuddyRepository buddyRepository;
    private final FindStudentService findStudentService;
    private final ChatRequestService chatRequestService;
    private final ChatService chatService;

    private final String SAME_UNIVERSITY_TYPE = "SAME_UNIVERSITY";
    private final String DIFFERENT_UNIVERSITY_TYPE = "DIFFERENT_UNIVERSITY";
    private final String SAME_GENDER_TYPE = "SAME_GENDER";

    public CreateChatroomResponse requestMatch(Long studentId, MatchCreateRequest request) {
        Student student = findStudentService.findByStudentId(studentId);
        boolean isKorean = student.getIsKorean();
        Long universityId = student.getUniversity().getId();
        String gender = student.getGender().name();
        Set<Long> existingBuddies = buddyRepository.findBuddyIdsByStudentId(studentId);
        Optional<MatchRequest> optionalMatchRequest = findValidMatchRequest(
                student,
                isKorean,
                request.universityType(),
                request.genderType(),
                universityId,
                gender,
                existingBuddies
        );
        if (optionalMatchRequest.isPresent()) {
            Student matchedStudent = optionalMatchRequest.get().getStudent();
            Buddy requestedBuddy = Buddy.builder()
                    .student(student)
                    .buddyId(matchedStudent.getId())
                    .build();
            Buddy MatchedBuddy = Buddy.builder()
                    .student(matchedStudent)
                    .buddyId(studentId)
                    .build();
            buddyRepository.save(requestedBuddy);
            buddyRepository.save(MatchedBuddy);
            matchRepository.delete(optionalMatchRequest.get());
            Chatroom chatroom = chatService.createChatroom(student, matchedStudent);
            return CreateChatroomResponse.from(chatroom, matchedStudent, true);
        }
        MatchRequest matchRequest = MatchRequest.builder()
                .student(student)
                .isKorean(isKorean)
                .build();
        matchRepository.save(matchRequest);
        return CreateChatroomResponse.from();
    }

    private Optional<MatchRequest> findValidMatchRequest(
            Student student, boolean isKorean, String universityType, String genderType,
            Long universityId, String studentGender, Set<Long> existingBuddies) {
        return matchRepository.findAllMatches(isKorean).stream()
                .filter(matchedRequest -> {
                    Student matchedStudent = matchedRequest.getStudent();
                    Long matchedUniversityId = matchedStudent.getUniversity().getId();
                    String matchedGender = matchedStudent.getGender().name();
                    if (existingBuddies.contains(matchedStudent.getId())) {
                        return false;
                    }
                    if (chatRequestService.isAlreadyExistChatroom(student, matchedStudent)) {
                        return false;
                    }
                    if (SAME_UNIVERSITY_TYPE.equals(universityType) && !matchedUniversityId.equals(universityId)) {
                        return false;
                    }
                    if (DIFFERENT_UNIVERSITY_TYPE.equals(universityType) && matchedUniversityId.equals(universityId)) {
                        return false;
                    }
                    if (SAME_GENDER_TYPE.equals(genderType) && !studentGender.equals(matchedGender)) {
                        return false;
                    }
                    return true;
                })
                .findFirst();
    }
}
