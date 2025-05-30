package com.team.buddyya.student.domain;

import com.team.buddyya.auth.domain.AuthToken;
import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.chatting.domain.ChatroomStudent;
import com.team.buddyya.common.domain.BaseTime;
import com.team.buddyya.notification.domain.ExpoToken;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "student")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Student extends BaseTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 11, nullable = false, unique = true)
    private String phoneNumber;

    @Column(length = 64, nullable = false)
    private String name;

    @Column(length = 64, nullable = false)
    private String country;

    @Column(name = "certificated", nullable = false)
    @ColumnDefault("false")
    private Boolean isCertificated;

    @Column(name = "korean", nullable = false)
    private Boolean isKorean;

    @Column(name = "deleted", nullable = false)
    private Boolean isDeleted;

    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Avatar avatar;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "email", unique = true)
    private String email;

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StudentMajor> majors;

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StudentLanguage> languages;

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StudentInterest> interests;

    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private ProfileImage profileImage;

    @Column(name = "character_profile_image", nullable = false)
    private String characterProfileImage;

    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private AuthToken authToken;

    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private StudentIdCard studentIdCard;

    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private ExpoToken expoToken;

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatroomStudent> chatroomStudents;

    @Column(name = "banned", nullable = false)
    private Boolean isBanned;

    @Column(name = "ban_end_time")
    private LocalDateTime banEndTime;

    @Column(name = "ban_reason")
    private String banReason;

    @Builder
    public Student(String name, String phoneNumber, String country, Boolean isKorean, Role role, University university,
                   Gender gender, String characterProfileImage) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.isKorean = isKorean;
        this.role = role;
        this.university = university;
        this.gender = gender;
        this.majors = new ArrayList<>();
        this.languages = new ArrayList<>();
        this.interests = new ArrayList<>();
        this.isCertificated = false;
        this.characterProfileImage = characterProfileImage;
        this.isDeleted = false;
        this.isBanned = false;
        this.banReason = null;
    }

    public void updateIsCertificated(boolean isCertificated) {
        this.isCertificated = isCertificated;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public void setProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public void setStudentIdCard(StudentIdCard studentIdCard) {
        this.studentIdCard = studentIdCard;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.isCertificated = false;
        this.phoneNumber = "d_" + UUID.randomUUID().toString().substring(0, 9);
        this.email = "d_" + UUID.randomUUID().toString().substring(0, 9);
        this.name = "UNKNOWN";
    }

    public boolean checkAndUpdateBanStatus() {
        if (this.isBanned && this.banEndTime != null) {
            if (LocalDateTime.now().isAfter(this.banEndTime)) {
                unban();
                return false;
            }
        }
        return this.isBanned;
    }

    public void ban(int days, String banReason) {
        this.isBanned = true;
        this.banEndTime = LocalDateTime.now().plusDays(days);
        this.banReason = banReason;
    }

    public void unban() {
        this.isBanned = false;
        this.banEndTime = null;
        this.banReason = null;
    }
}
