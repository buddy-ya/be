package com.team.buddyya.match.service;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;
import com.team.buddyya.chatting.exception.ChatException;
import com.team.buddyya.chatting.exception.ChatExceptionType;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.chatting.repository.ChatroomStudentRepository;
import com.team.buddyya.chatting.service.ChatRequestService;
import com.team.buddyya.chatting.service.ChatService;
import com.team.buddyya.match.domain.*;
import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.dto.response.MatchResponse;
import com.team.buddyya.match.exception.MatchException;
import com.team.buddyya.match.exception.MatchExceptionType;
import com.team.buddyya.match.repository.MatchedHistoryRepository;
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
    private final MatchedHistoryRepository matchedHistoryRepository;
    private final FindStudentService findStudentService;
    private final ChatRequestService chatRequestService;
    private final ChatService chatService;
    private final ChatroomRepository chatroomRepository;
    private final ChatroomStudentRepository chatroomStudentRepository;
    private final NotificationService notificationService;

    @Override
    public MatchResponse requestMatch(Long studentId, MatchCreateRequest request) {
        Student student = findStudentService.findByStudentId(studentId);
        boolean isKorean = student.getIsKorean();
        Long universityId = student.getUniversity().getId();
        Gender gender = student.getGender();
        UniversityType universityType = UniversityType.fromValue(request.universityType());
        GenderType genderType = GenderType.fromValue(request.genderType());
        Set<Long> existingBuddies = matchedHistoryRepository.findBuddyIdsByStudentId(studentId);
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
    public void deleteMatch(Long studentId) {
        Student student = findStudentService.findByStudentId(studentId);
        matchRequestRepository.deleteByStudent(student);
    }

    @Override
    public MatchResponse findMatchStatus(Long studentId) {
        Optional<MatchRequest> matchRequest = matchRequestRepository.findByStudentId(studentId);
        if(matchRequest.isEmpty()){
            return MatchResponse.from(MatchRequestStatus.MATCH_NOT_REQUESTED.getDisplayName());
        }
        MatchRequestStatus status = matchRequest.get().getMatchRequestStatus();
        if(status.equals(MatchRequestStatus.MATCH_PENDING)){
            return MatchResponse.from(MatchRequestStatus.MATCH_PENDING.getDisplayName());
        }
        if(status.equals(MatchRequestStatus.MATCH_SUCCESS)) {
            MatchedHistory recentMatchedHistory = matchedHistoryRepository.findMostRecentMatchedHistoryByStudentId(studentId);
            Chatroom chatroom = chatroomRepository.findByUserAndBuddy(studentId, recentMatchedHistory.getBuddyId())
                    .orElseThrow(()-> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
            ChatroomStudent chatroomStudent = chatroomStudentRepository.findByChatroomAndStudentId(chatroom, studentId)
                    .orElseThrow(() -> new ChatException(ChatExceptionType.USER_NOT_PART_OF_CHATROOM));
            if(chatroomStudent.getIsExited().equals(true)){
                throw new ChatException(ChatExceptionType.CHATROOM_ALREADY_EXITED);
            }
            Student matchedStudent = findStudentService.findByStudentId(recentMatchedHistory.getBuddyId());
            return MatchResponse.from(chatroom, matchedStudent, matchRequest.get().getMatchRequestStatus().getDisplayName());
        }
        throw new MatchException(MatchExceptionType.UNEXPECTED_MATCH_STATUS);
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

    private MatchResponse processMatchSuccess(Student student, MatchRequest matchRequest) {
        Student matchedStudent = matchRequest.getStudent();
        MatchedHistory requestedMatchedHistory = MatchedHistory.builder()
                .student(student)
                .buddyId(matchedStudent.getId())
                .build();
        MatchedHistory existingMatchedHistory = MatchedHistory.builder()
                .student(matchedStudent)
                .buddyId(student.getId())
                .build();
        matchedHistoryRepository.save(requestedMatchedHistory);
        matchedHistoryRepository.save(existingMatchedHistory);
        matchRequest.updateMatchRequestStatusSuccess();
        Chatroom chatroom = chatService.createChatroom(student, matchedStudent);
        notificationService.sendMatchSuccessNotification(student, chatroom.getId());
        notificationService.sendMatchSuccessNotification(matchedStudent, chatroom.getId());
        return MatchResponse.from(chatroom, matchedStudent, MatchRequestStatus.MATCH_SUCCESS.getDisplayName());
    }

    private MatchResponse createMatchRequest(Student student, boolean isKorean,
                                             UniversityType universityType, GenderType genderType,
                                             Long universityId, Gender gender) {
        MatchRequest matchRequest = MatchRequest.builder()
                .student(student)
                .isKorean(isKorean)
                .universityType(universityType)
                .genderType(genderType)
                .matchRequestStatus(MatchRequestStatus.MATCH_PENDING)
                .universityId(universityId)
                .gender(gender)
                .build();
        matchRequestRepository.save(matchRequest);
        return MatchResponse.from(MatchRequestStatus.MATCH_PENDING.getDisplayName());
    }
}
