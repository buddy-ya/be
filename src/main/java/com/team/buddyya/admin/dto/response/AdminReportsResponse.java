package com.team.buddyya.admin.dto.response;

import com.team.buddyya.report.domain.Report;
import com.team.buddyya.report.domain.ReportType;

import java.util.List;
import java.util.stream.Collectors;

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
    public static AdminReportsResponse from(Report report) {
        return new AdminReportsResponse(
                report.getId(),
                report.getType(),
                report.getType() == ReportType.CHATROOM ? report.getReportedId() : null,
                report.getReportUserId(),
                report.getReportedUserId(),
                report.getType() == ReportType.FEED ? report.getTitle() : null,
                getContent(report),
                report.getReason(),
                getImageUrls(report)
        );
    }

    private static String getContent(Report report) {
        return switch (report.getType()) {
            case FEED, COMMENT -> report.getContent();
            default -> null;
        };
    }

    private static List<String> getImageUrls(Report report) {
        if (report.getImages() == null || report.getImages().isEmpty()) {
            return null;
        }
        return report.getImages().stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList());
    }

}
