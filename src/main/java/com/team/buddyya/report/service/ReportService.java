package com.team.buddyya.report.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.chatting.service.ChatService;
import com.team.buddyya.feed.service.CommentService;
import com.team.buddyya.report.domain.Report;
import com.team.buddyya.report.domain.ReportType;
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
    private final CommentService commentService;
    private final ChatService chatService;

    public void createReport(StudentInfo studentInfo, ReportRequest request) {
        validateReportedContent(request.type(), request.reportedId());
        Report report = Report.builder()
                .type(request.type())
                .reportedId(request.reportedId())
                .reportUserId(studentInfo.id())
                .reportedUserId(request.reportedUserId())
                .content(request.content())
                .build();
        reportRepository.save(report);
    }

    private void validateReportedContent(ReportType type, Long id) {
        switch (type) {
            case FEED:
                commentService.findFeedByFeedId(id);
                break;
            case COMMENT:
                commentService.findCommentByCommentId(id);
                break;
            case CHATROOM:
                chatService.findByChatroomByChatroomId(id);
                break;
        }
    }
}
