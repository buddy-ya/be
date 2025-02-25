package com.team.buddyya.admin.dto.response;

import com.team.buddyya.report.domain.Report;
import com.team.buddyya.report.domain.ReportType;

import java.util.List;

public record AdminReportsResponse(
        Long id,
        ReportType type,
        Long reportedId,
        Long reportUserId,
        Long reportedUserId,
        String title,
        String content,
        String reason,
        List<String> imageUrls
) {
    public static AdminReportsResponse from(Report report, List<String> imageUrls) {
        return new AdminReportsResponse(
                report.getId(),
                report.getType(),
                report.getType() == ReportType.CHATROOM ? report.getReportedId() : null,
                report.getReportUserId(),
                report.getReportedUserId(),
                report.getType() == ReportType.FEED ? report.getTitle() : null,
                getContent(report),
                report.getReason(),
                imageUrls
        );
    }

    private static String getContent(Report report) {
        return switch (report.getType()) {
            case FEED, COMMENT -> report.getContent();
            default -> null;
        };
    }
}
