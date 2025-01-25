package com.team.buddyya.report.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.report.domain.Report;
import com.team.buddyya.report.dto.ReportRequest;
import com.team.buddyya.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public void createReport(StudentInfo studentInfo, ReportRequest request) {
        Report report = Report.builder()
                .type(request.type())
                .targetId(request.targetId())
                .reporterId(studentInfo.id())
                .reportedUserId(request.reportedUserId())
                .reason(request.reason())
                .build();
        reportRepository.save(report);
    }
}
