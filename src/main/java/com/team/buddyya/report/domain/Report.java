package com.team.buddyya.report.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private Long reporterId;

    @Column(nullable = false)
    private Long reportedUserId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Builder
    public Report(ReportType type, Long targetId, Long reporterId, Long reportedUserId, String reason) {
        this.type = type;
        this.targetId = targetId;
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
        this.reason = reason;
    }
}
