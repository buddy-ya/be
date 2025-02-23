package com.team.buddyya.report.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.chatting.service.ChatService;
import com.team.buddyya.feed.service.CommentService;
import com.team.buddyya.feed.service.FeedService;
import com.team.buddyya.report.domain.Report;
import com.team.buddyya.report.domain.ReportImage;
import com.team.buddyya.report.domain.ReportType;
import com.team.buddyya.report.dto.ReportRequest;
import com.team.buddyya.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final FeedService feedService;
    private final CommentService commentService;
    private final ChatService chatService;

    public void createReport(StudentInfo studentInfo, ReportRequest request) {
        validateReportedContent(request.type(), request.reportedId());
        Report report = Report.builder()
                .type(request.type())
                .reportedId(request.type() == ReportType.CHATROOM ? request.reportedId() : null)
                .reportUserId(studentInfo.id())
                .reportedUserId(request.reportedUserId())
                .title(getTitle(request))
                .content(getContent(request))
                .reason(request.reason())
                .images(getReportImages(request))
                .build();
        reportRepository.save(report);
    }

    private String getTitle(ReportRequest request) {
        if (request.type() == ReportType.FEED) {
            return feedService.findFeedByFeedId(request.reportedId()).getTitle();
        }
        return null;
    }

    private String getContent(ReportRequest request) {
        if (request.type() == ReportType.FEED) {
            return feedService.findFeedByFeedId(request.reportedId()).getContent();
        } else if (request.type() == ReportType.COMMENT) {
            return commentService.findCommentByCommentId(request.reportedId()).getContent();
        }
        return null;
    }

    private List<ReportImage> getReportImages(ReportRequest request) {
        if (request.type() == ReportType.FEED) {
            return feedService.findFeedByFeedId(request.reportedId()).getImages().stream()
                    .map(image -> new ReportImage(image.getUrl()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private void validateReportedContent(ReportType type, Long id) {
        switch (type) {
            case FEED:
                feedService.findFeedByFeedId(id);
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
