package com.team.buddyya.student.domain;

import com.team.buddyya.match.exception.MatchException;
import com.team.buddyya.match.exception.MatchExceptionType;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "matching_profile")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class MatchingProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "buddy_activity", columnDefinition = "TEXT")
    private String buddyActivity;

    @Column(name = "completed")
    private boolean isCompleted;

    public MatchingProfile(Student student) {
        this.student = student;
        this.isCompleted = false;
    }

    public void updateIntroduction(String introduction) {
        if (introduction != null && introduction.length() < 10) {
            throw new StudentException(StudentExceptionType.INVALID_INTRODUCTION_LENGTH);
        }
        this.introduction = introduction;
        updateCompletedStatus();
    }

    public void updateActivity(String buddyActivity) {
        if (buddyActivity != null && buddyActivity.length() < 10) {
            throw new StudentException(StudentExceptionType.INVALID_ACTIVITIES_LENGTH);
        }
        this.buddyActivity = buddyActivity;
        updateCompletedStatus();
    }

    private void updateCompletedStatus() {
        boolean hasValidIntroduction = (this.introduction != null) && (this.introduction.length() >= 10);
        boolean hasValidActivities = (this.buddyActivity != null) && (this.buddyActivity.length() >= 10);
        this.isCompleted = hasValidIntroduction && hasValidActivities;
    }

    public void validateCompletion() {
        if (this.isCompleted == false) {
            throw new MatchException(MatchExceptionType.MATCH_PROFILE_NOT_COMPLETED);
        }
    }
}
