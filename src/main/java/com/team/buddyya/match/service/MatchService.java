package com.team.buddyya.match.service;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.service.ChatRequestService;
import com.team.buddyya.chatting.service.ChatService;
import com.team.buddyya.match.domain.MatchedHistory;
import com.team.buddyya.match.domain.MatchRequest;
import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.dto.response.MatchCreateResponse;
import com.team.buddyya.match.repositorry.BuddyRepository;
import com.team.buddyya.match.repositorry.MatchRequestRepository;
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

    private final MatchRequestRepository matchRequestRepository;
    private final BuddyRepository buddyRepository;
    private final FindStudentService findStudentService;
    private final ChatRequestService chatRequestService;
    private final ChatService chatService;
    private final NotificationService notificationService;

    private final String MATCH_STATUS_PENDING = "pending";
    private final String MATCH_STATUS_SUCCESS = "success";
    private final String SAME_UNIVERSITY_TYPE = "SAME_UNIVERSITY";
    private final String DIFFERENT_UNIVERSITY_TYPE = "DIFFERENT_UNIVERSITY";
    private final String SAME_GENDER_TYPE = "SAME_GENDER";

    public MatchCreateResponse requestMatch(Long studentId, MatchCreateRequest request) {
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
            return processMatchSuccess(student, optionalMatchRequest.get());
        }
        return createMatchRequest(student, isKorean);
    }

    private Optional<MatchRequest> findValidMatchRequest(
            Student student, boolean isKorean, String universityType, String genderType,
            Long universityId, String studentGender, Set<Long> existingBuddies) {
        return matchRequestRepository.findAllMatches(isKorean).stream()
                .filter(matchRequest -> {
                    Student matchedStudent = matchRequest.getStudent();
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

    private MatchCreateResponse processMatchSuccess(Student student, MatchRequest matchRequest) {
        Student matchedStudent = matchRequest.getStudent();
        MatchedHistory requestedMatchedHistory = MatchedHistory.builder()
                .student(student)
                .buddyId(matchedStudent.getId())
                .build();
        MatchedHistory matchedMatchedHistory = MatchedHistory.builder()
                .student(matchedStudent)
                .buddyId(student.getId())
                .build();
        buddyRepository.save(requestedMatchedHistory);
        buddyRepository.save(matchedMatchedHistory);
        matchRequestRepository.delete(matchRequest);
        Chatroom chatroom = chatService.createChatroom(student, matchedStudent);
        notificationService.sendMatchSuccessNotification(student, chatroom.getId());
        notificationService.sendMatchSuccessNotification(matchedStudent, chatroom.getId());
        return MatchCreateResponse.from(chatroom, matchedStudent, true, MATCH_STATUS_SUCCESS);
    }

    private MatchCreateResponse createMatchRequest(Student student, boolean isKorean) {
        MatchRequest matchRequest = MatchRequest.builder()
                .student(student)
                .isKorean(isKorean)
                .build();
        matchRequestRepository.save(matchRequest);
        return MatchCreateResponse.from(MATCH_STATUS_PENDING);
    }
}
