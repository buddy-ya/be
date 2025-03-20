package com.team.buddyya.report.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.exception.ChatException;
import com.team.buddyya.chatting.exception.ChatExceptionType;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.repository.CommentRepository;
import com.team.buddyya.feed.repository.FeedRepository;
import com.team.buddyya.report.domain.Report;
import com.team.buddyya.report.domain.ReportImage;
import com.team.buddyya.report.domain.ReportType;
import com.team.buddyya.report.dto.ReportRequest;
import com.team.buddyya.report.exception.ReportException;
import com.team.buddyya.report.exception.ReportExceptionType;
import com.team.buddyya.report.repository.ReportImageRepository;
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
    private final ChatroomRepository chatRoomRepository;
    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;
    private final ReportImageRepository reportImageRepository;

    public void createReport(StudentInfo studentInfo, ReportRequest request) {
        validateAlreadyReport(studentInfo, request);
        validateReportedContent(request.type(), request.reportedId());
        Report report = Report.builder()
                .type(request.type())
                .reportedId(getReportedId(request))
                .reportUserId(studentInfo.id())
                .reportedUserId(request.reportedUserId())
                .title(getTitle(request))
                .content(getContent(request))
                .reason(request.reason())
                .build();
        reportRepository.save(report);
        List<ReportImage> reportImages = getReportImages(request, report);
        reportImages.forEach(reportImageRepository::save);
    }

    private void validateAlreadyReport(StudentInfo studentInfo, ReportRequest request) {
        boolean alreadyReported = reportRepository.existsByReportUserIdAndTypeAndReportedId(
                studentInfo.id(), request.type(), request.reportedId());
        if (alreadyReported) {
            throw new ReportException(ReportExceptionType.ALREADY_REPORTED);
        }
    }

    private void validateReportedContent(ReportType type, Long id) {
        switch (type) {
            case FEED:
                findFeedById(id);
                break;
            case COMMENT:
                findCommentById(id);
                break;
            case CHATROOM:
                findByChatroomByChatroomId(id);
                break;
        }
    }

    private Feed findFeedById(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.COMMENT_NOT_FOUND));
    }

    private Chatroom findByChatroomByChatroomId(Long chatroomId) {
        return chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
    }

    private String getTitle(ReportRequest request) {
        if (request.type() == ReportType.FEED) {
            return findFeedById(request.reportedId()).getTitle();
        }
        return null;
    }

    private String getContent(ReportRequest request) {
        if (request.type() == ReportType.FEED) {
            return findFeedById(request.reportedId()).getContent();
        } else if (request.type() == ReportType.COMMENT) {
            return findCommentById(request.reportedId()).getContent();
        }
        return null;
    }

    private Long getReportedId(ReportRequest request) {
        return request.reportedId();
    }

    private List<ReportImage> getReportImages(ReportRequest request, Report report) {
        if (request.type() == ReportType.FEED) {
            return findFeedById(request.reportedId()).getImages().stream()
                    .map(image -> new ReportImage(image.getUrl(), report))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
