package com.team.buddyya.report.dto;

import com.team.buddyya.report.domain.ReportType;

public record ReportRequest(
        ReportType type,
        Long targetId,
        Long reportedUserId,
        String reason
) {
}
