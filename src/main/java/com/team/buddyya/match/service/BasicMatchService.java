package com.team.buddyya.match.service;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.service.ChatRequestService;
import com.team.buddyya.chatting.service.ChatService;
import com.team.buddyya.match.domain.GenderType;
import com.team.buddyya.match.domain.MatchedHistory;
import com.team.buddyya.match.domain.MatchRequest;
import com.team.buddyya.match.domain.UniversityType;
import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.dto.response.MatchCreateResponse;
import com.team.buddyya.match.dto.response.MatchStatusResponse;
import com.team.buddyya.match.repository.BuddyRepository;
import com.team.buddyya.match.repository.MatchRequestRepository;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.student.domain.Gender;
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
public class BasicMatchService implements MatchService {

    private final MatchRequestRepository matchRequestRepository;
    private final BuddyRepository buddyRepository;
    private final FindStudentService findStudentService;
    private final ChatRequestService chatRequestService;
    private final ChatService chatService;
    private final NotificationService notificationService;

    private static final String MATCH_STATUS_PENDING = "pending";
    private static final String MATCH_STATUS_SUCCESS = "success";
    private static final String MATCH_STATUS_NOT_REQUESTED = "not_requested";

    @Override
    public MatchCreateResponse requestMatch(Long studentId, MatchCreateRequest request) {
        Student student = findStudentService.findByStudentId(studentId);
        boolean isKorean = student.getIsKorean();
        Long universityId = student.getUniversity().getId();
        Gender gender = student.getGender();
        UniversityType universityType = UniversityType.fromValue(request.universityType());
        GenderType genderType = GenderType.fromValue(request.genderType());
        Set<Long> existingBuddies = buddyRepository.findBuddyIdsByStudentId(studentId);
        Optional<MatchRequest> optionalMatchRequest = findValidMatchRequest(
                isKorean,
                universityType,
                genderType,
                universityId,
                gender,
                existingBuddies
        );
        if (optionalMatchRequest.isPresent()) {
            return processMatchSuccess(student, optionalMatchRequest.get());
        }
        return createMatchRequest(student, isKorean, universityType, genderType, universityId, gender);
    }

    @Override
    public MatchStatusResponse findMatchStatus(Long studentId) {
        boolean exists = matchRequestRepository.existsByStudentId(studentId);
        return MatchStatusResponse.from(exists ? MATCH_STATUS_PENDING : MATCH_STATUS_NOT_REQUESTED);
    }

    private Optional<MatchRequest> findValidMatchRequest(
            boolean isKorean, UniversityType universityType, GenderType genderType,
            Long universityId, Gender studentGender, Set<Long> existingBuddies) {
        return matchRequestRepository.findAllMatches(isKorean).stream()
                .filter(matchRequest -> isValidMatchRequest(matchRequest, universityType, genderType, universityId, studentGender, existingBuddies))
                .findFirst();
    }

    private boolean isValidMatchRequest(
            MatchRequest matchRequest, UniversityType requestedUniversityType, GenderType requestedGenderType,
            Long requestedUniversityId, Gender requestedGender, Set<Long> existingBuddies) {
        if (existingBuddies.contains(matchRequest.getStudent().getId())) {
            return false;
        }
        if (chatRequestService.isAlreadyExistChatroom(matchRequest.getStudent(), matchRequest.getStudent())) {
            return false;
        }
        return isUniversityMatch(matchRequest.getUniversityType(), requestedUniversityType, matchRequest.getUniversityId(), requestedUniversityId)
                && isGenderMatch(matchRequest.getGenderType(), requestedGenderType, matchRequest.getGender(), requestedGender);
    }


    private boolean isUniversityMatch(UniversityType matchRequestUniversityType, UniversityType requestedUniversityType,
                                      Long matchRequestUniversityId, Long requestedUniversityId) {
        if (matchRequestUniversityType == UniversityType.SAME_UNIVERSITY && requestedUniversityType == UniversityType.SAME_UNIVERSITY) {
            return matchRequestUniversityId.equals(requestedUniversityId);
        }
        if (matchRequestUniversityType == UniversityType.DIFFERENT_UNIVERSITY && requestedUniversityType == UniversityType.DIFFERENT_UNIVERSITY) {
            return !matchRequestUniversityId.equals(requestedUniversityId);
        }
        return false;
    }

    private boolean isGenderMatch(GenderType matchRequestGenderType, GenderType requestedGenderType,
                                  Gender matchRequestGender, Gender requestedGender) {
        if (matchRequestGenderType == GenderType.SAME_GENDER && requestedGenderType == GenderType.SAME_GENDER) {
            return matchRequestGender == requestedGender;
        }
        if (matchRequestGenderType == GenderType.SAME_GENDER || requestedGenderType == GenderType.SAME_GENDER) {
            return matchRequestGender == requestedGender;
        }
        return true;
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

    private MatchCreateResponse createMatchRequest(Student student, boolean isKorean,
                                                   UniversityType universityType, GenderType genderType,
                                                   Long universityId, Gender gender) {
        MatchRequest matchRequest = MatchRequest.builder()
                .student(student)
                .isKorean(isKorean)
                .universityType(universityType)
                .genderType(genderType)
                .universityId(universityId)
                .gender(gender)
                .build();
        matchRequestRepository.save(matchRequest);
        return MatchCreateResponse.from(MATCH_STATUS_PENDING);
    }
}
