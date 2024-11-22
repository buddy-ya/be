package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Category;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedUserAction;
import com.team.buddyya.feed.dto.request.FeedCreateRequest;
import com.team.buddyya.feed.dto.request.FeedListRequest;
import com.team.buddyya.feed.dto.request.FeedUpdateRequest;
import com.team.buddyya.feed.dto.response.FeedListItemResponse;
import com.team.buddyya.feed.dto.response.FeedListResponse;
import com.team.buddyya.feed.dto.response.FeedResponse;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.respository.FeedRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedService {

    private final LikeSevice likeSevice;
    private final BookmarkService bookmarkService;
    private final CategoryService categoryService;
    private final FeedRepository feedRepository;
    private final StudentRepository studentRepository;

    public Feed findFeedByFeedId(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
    }

    public FeedListResponse getFeeds(StudentInfo studentInfo, FeedListRequest request) {
        Category category = categoryService.getCategory(request.category());
        PageRequest pageRequest = PageRequest.of(request.page(), request.size(),
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Feed> feeds = feedRepository.findAllByCategory(category, pageRequest);
        List<FeedListItemResponse> response = feeds.getContent().stream()
                .map(feed -> createFeedListItemResponse(feed, studentInfo.id()))
                .toList();
        return FeedListResponse.from(feeds, response);
    }

    public FeedResponse getFeed(StudentInfo studentInfo, Long feedId) {
        Feed feed = findFeedByFeedId(feedId);
        FeedUserAction userAction = getUserAction(studentInfo.id(), feedId);
        return FeedResponse.from(feed, userAction.isLiked(), userAction.isBookmarked());
    }

    public void createFeed(StudentInfo studentInfo, FeedCreateRequest request) {
        Category category = categoryService.getCategory(request.category());
        Student student = studentRepository.findById(studentInfo.id())
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
        Feed feed = Feed.builder()
                .title(request.title())
                .content(request.content())
                .student(student)
                .category(category)
                .university(student.getUniversity())
                .build();
        feedRepository.save(feed);
    }

    public void updateFeed(StudentInfo studentInfo, Long feedId, FeedUpdateRequest request) {
        Feed feed = findFeedByFeedId(feedId);
        validateFeedOwner(studentInfo.id(), feed);
        Category category = categoryService.getCategory(request.category());
        feed.updateFeed(request.title(), request.content(), category);
    }

    public void deleteFeed(StudentInfo studentInfo, Long feedId) {
        Feed feed = findFeedByFeedId(feedId);
        validateFeedOwner(studentInfo.id(), feed);
        feedRepository.delete(feed);
    }

    private void validateFeedOwner(Long studentId, Feed feed) {
        if (!studentId.equals(feed.getStudent().getId())) {
            throw new FeedException(FeedExceptionType.NOT_FEED_OWNER);
        }
    }

    private FeedListItemResponse createFeedListItemResponse(Feed feed, Long studentId) {
        FeedUserAction userAction = getUserAction(studentId, feed.getId());
        return FeedListItemResponse.from(feed, userAction.isLiked(), userAction.isBookmarked());
    }

    private FeedUserAction getUserAction(Long studentId, Long feedId) {
        boolean isLiked = likeSevice.existsByStudentIdAndFeedId(studentId, feedId);
        boolean isBookmarked = bookmarkService.existsByStudentIdAndFeedId(studentId, feedId);
        return FeedUserAction.from(isLiked, isBookmarked);
    }
}
