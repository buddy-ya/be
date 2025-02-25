package com.team.buddyya.admin.dto.response;

import com.team.buddyya.report.domain.Report;
import com.team.buddyya.report.domain.ReportType;

import java.util.List;

public record AdminReportResponse(
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
    public static AdminReportResponse from(Report report, List<String> imageUrls) {
        return new AdminReportResponse(
                report.getId(),
                report.getType(),
                getReportedId(report),
                report.getReportUserId(),
                report.getReportedUserId(),
                getTitle(report),
                getContent(report),
                report.getReason(),
                imageUrls
        );
    }

    private static Long getReportedId(Report report) {
        if (report.getType() == ReportType.CHATROOM) {
            return report.getReportedId();
        }
        return null;
    }

    private static String getTitle(Report report) {
        if (report.getType() == ReportType.FEED) {
            return report.getTitle();
        }
        return null;
    }

    private static String getContent(Report report) {
        return switch (report.getType()) {
            case FEED, COMMENT -> report.getContent();
            default -> null;
        };
    }
}
