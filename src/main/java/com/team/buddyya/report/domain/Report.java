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
    @Column(name = "type", nullable = false)
    private ReportType type;

    @Column(name = "reported_id", nullable = false)
    private Long reportedId;

    @Column(name = "report_user_id", nullable = false)
    private Long reportUserId;

    @Column(name = "reported_user_id", nullable = false)
    private Long reportedUserId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public Report(ReportType type, Long reportedId, Long reportUserId, Long reportedUserId, String content) {
        this.type = type;
        this.reportedId = reportedId;
        this.reportUserId = reportUserId;
        this.reportedUserId = reportedUserId;
        this.content = content;
    }
}
