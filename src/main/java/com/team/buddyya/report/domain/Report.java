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

    @Column(name = "reported_id")
    private Long reportedId;

    @Column(name = "report_user_id", nullable = false)
    private Long reportUserId;

    @Column(name = "reported_user_id", nullable = false)
    private Long reportedUserId;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String reason;

//    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ReportImage> images = new ArrayList<>();

    @Builder
    public Report(ReportType type, Long reportedId, Long reportUserId, Long reportedUserId,
                  String title, String content, String reason) {
        this.type = type;
        this.reportedId = reportedId;
        this.reportUserId = reportUserId;
        this.reportedUserId = reportedUserId;
        this.title = title;
        this.content = content;
        this.reason = reason;
//        if (images != null) {
//            this.images.addAll(images);
//            this.images.forEach(image -> image.setReport(this));
//        }
    }
}
