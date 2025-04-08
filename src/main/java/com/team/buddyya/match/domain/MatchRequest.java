package com.team.buddyya.match.domain;

import com.team.buddyya.common.domain.BaseTime;
import com.team.buddyya.student.domain.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_request")
public class MatchRequest extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "chatroom_id")
    private Long chatroomId;

    @Column(name = "university_id", nullable = false)
    private Long universityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "nationality_type", nullable = false)
    private NationalityType nationalityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "university_type", nullable = false)
    private UniversityType universityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_type", nullable = false)
    private GenderType genderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_request_status", nullable = false)
    private MatchRequestStatus matchRequestStatus;

    @Column(name = "chinese_available", nullable = false)
    private Boolean isChineseAvailable;

    @Builder
    public MatchRequest(Student student, Long chatroomId, Long universityId, NationalityType nationalityType, UniversityType universityType, GenderType genderType, MatchRequestStatus matchRequestStatus, Boolean isChineseAvailable) {
        this.student = student;
        this.chatroomId = chatroomId;
        this.nationalityType = nationalityType;
        this.universityId = universityId;
        this.universityType = universityType;
        this.genderType = genderType;
        this.matchRequestStatus = matchRequestStatus;
        this.isChineseAvailable = isChineseAvailable;
    }

    public void updateMatchRequestStatusSuccess() {
        this.matchRequestStatus = MatchRequestStatus.MATCH_SUCCESS;
    }

    public void updateChatroomId(Long chatroomId) {
        this.chatroomId = chatroomId;
    }
}
