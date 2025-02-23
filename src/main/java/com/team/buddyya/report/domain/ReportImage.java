package com.team.buddyya.report.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "report_image")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ReportImage {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Builder
    public ReportImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}