package com.team.buddyya.student.domain;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "block")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Block extends CreatedTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student blocker;

    @Column(name = "blocked_student_id", nullable = false)
    private Long blockedStudentId;

    @Builder
    public Block(Student blocker, Long blockedStudentId) {
        this.blocker = blocker;
        this.blockedStudentId = blockedStudentId;
    }
}
