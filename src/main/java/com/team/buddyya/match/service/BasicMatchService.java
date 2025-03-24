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
import com.team.buddyya.match.dto.response.MatchDeleteResponse;
import com.team.buddyya.match.dto.response.MatchResponse;
import com.team.buddyya.match.dto.response.MatchStatusResponse;
import com.team.buddyya.match.exception.MatchException;
import com.team.buddyya.match.exception.MatchExceptionType;
import com.team.buddyya.match.repository.MatchedHistoryRepository;
import com.team.buddyya.match.repository.MatchRequestRepository;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.student.domain.Gender;
import com.team.buddyya.student.domain.Point;
import com.team.buddyya.student.domain.PointType;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.repository.PointRepository;
import com.team.buddyya.student.service.FindStudentService;
import com.team.buddyya.student.service.PointService;
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
    private final ChatService chatService;
    private final ChatroomRepository chatroomRepository;
    private final ChatroomStudentRepository chatroomStudentRepository;
    private final NotificationService notificationService;
    private final PointService pointService;
    private final PointRepository pointRepository;

    @Override
    public MatchResponse requestMatch(Long studentId, MatchCreateRequest request) {
        Student student = findStudentService.findByStudentId(studentId);
        Point point = pointService.updatePoint(student, PointType.MATCH_REQUEST);
        boolean isKorean = student.getIsKorean();
        Long universityId = student.getUniversity().getId();
        Gender gender = student.getGender();
        UniversityType universityType = UniversityType.fromValue(request.universityType());
        GenderType genderType = GenderType.fromValue(request.genderType());
        Set<Long> existingBuddies = matchedHistoryRepository.findBuddyIdsByStudentId(studentId);
        Optional<MatchRequest> optionalMatchRequest = findValidMatchRequest(
                student,
                isKorean,
                universityType,
                genderType,
                universityId,
                gender,
                existingBuddies
        );
        if (optionalMatchRequest.isPresent()) {
            return processMatchSuccess(student, universityType, genderType, optionalMatchRequest.get(), point);
        }
        MatchRequest newMatchRequest = createMatchRequest(student, universityType, genderType, MatchRequestStatus.MATCH_PENDING, universityId);
        return MatchResponse.from(newMatchRequest, point);
    }

    @Override
    public MatchDeleteResponse deleteMatch(Long studentId) {
        MatchRequest matchRequest = matchRequestRepository.findByStudentId(studentId)
                .orElseThrow(() -> new MatchException(MatchExceptionType.MATCH_REQUEST_NOT_FOUND));
        MatchRequestStatus matchRequestStatus = matchRequest.getMatchRequestStatus();
        Point point;
        if (matchRequestStatus.equals(MatchRequestStatus.MATCH_PENDING)) {
            point = pointService.updatePoint(matchRequest.getStudent(), PointType.CANCEL_MATCH_REQUEST);
        } else {
            point = pointRepository.findByStudent(matchRequest.getStudent()).get();
        }
        matchRequestRepository.delete(matchRequest);
        return MatchDeleteResponse.from(point);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchStatusResponse findMatchStatus(Long studentId) {
        Optional<MatchRequest> matchRequest = matchRequestRepository.findByStudentId(studentId);
        if (matchRequest.isEmpty()) {
            return MatchStatusResponse.from(MatchRequestStatus.MATCH_NOT_REQUESTED.getDisplayName());
        }
        MatchRequestStatus status = matchRequest.get().getMatchRequestStatus();
        if (status.equals(MatchRequestStatus.MATCH_PENDING)) {
            return MatchStatusResponse.from(matchRequest.get());
        }
        if (status.equals(MatchRequestStatus.MATCH_SUCCESS)) {
            MatchedHistory recentMatchedHistory = matchedHistoryRepository.findMostRecentMatchedHistoryByStudentId(studentId);
            Chatroom chatroom = chatroomRepository.findByUserAndBuddy(studentId, recentMatchedHistory.getBuddyId())
                    .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
            ChatroomStudent chatroomStudent = chatroomStudentRepository.findByChatroomAndStudentId(chatroom, studentId)
                    .orElseThrow(() -> new ChatException(ChatExceptionType.USER_NOT_PART_OF_CHATROOM));
            boolean isExited = chatroomStudent.getIsExited().equals(true);
            Student matchedStudent = findStudentService.findByStudentId(recentMatchedHistory.getBuddyId());
            return MatchStatusResponse.from(chatroom, matchedStudent, matchRequest.get(), isExited);
        }
        throw new MatchException(MatchExceptionType.UNEXPECTED_MATCH_STATUS);
    }

    private Optional<MatchRequest> findValidMatchRequest(
            Student requestedStudent, boolean isKorean, UniversityType universityType, GenderType genderType,
            Long universityId, Gender studentGender, Set<Long> existingBuddies) {
        return matchRequestRepository.findAllPendingMatches(isKorean).stream()
                .filter(matchRequest -> isValidMatchRequest(requestedStudent, matchRequest, universityType, genderType, universityId, studentGender, existingBuddies))
                .findFirst();
    }

    private boolean isValidMatchRequest(
            Student requestedStudent, MatchRequest matchRequest, UniversityType requestedUniversityType, GenderType requestedGenderType,
            Long requestedUniversityId, Gender requestedGender, Set<Long> existingBuddies) {
        Student matchStudent = matchRequest.getStudent();
        if (existingBuddies.contains(matchStudent.getId())) {
            return false;
        }
        boolean isAlreadyExistChatroom = chatroomRepository.findByUserAndBuddy(requestedStudent.getId(), matchStudent.getId()).isPresent();
        if (isAlreadyExistChatroom) {
            return false;
        }
        return isUniversityMatch(matchRequest.getUniversityType(), requestedUniversityType, matchRequest.getUniversityId(), requestedUniversityId)
                && isGenderMatch(matchRequest.getGenderType(), requestedGenderType, matchStudent.getGender(), requestedGender);
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

    private MatchResponse processMatchSuccess(Student student, UniversityType universityType, GenderType genderType, MatchRequest matchRequest, Point point) {
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
        MatchRequest newMatchRequest = createMatchRequest(student, universityType, genderType, MatchRequestStatus.MATCH_SUCCESS, student.getUniversity().getId());
        matchRequest.updateMatchRequestStatusSuccess();
        Chatroom chatroom = chatService.createChatroom(student, matchedStudent);
        notificationService.sendMatchSuccessNotification(student, chatroom.getId());
        notificationService.sendMatchSuccessNotification(matchedStudent, chatroom.getId());
        return MatchResponse.from(chatroom, matchedStudent, newMatchRequest, point, false);
    }

    private MatchRequest createMatchRequest(Student student,
                                            UniversityType universityType, GenderType genderType, MatchRequestStatus status,
                                            Long universityId) {
        MatchRequest matchRequest = MatchRequest.builder()
                .student(student)
                .isKorean(student.getIsKorean())
                .universityType(universityType)
                .genderType(genderType)
                .matchRequestStatus(status)
                .universityId(universityId)
                .build();
        return matchRequestRepository.save(matchRequest);
    }
}
