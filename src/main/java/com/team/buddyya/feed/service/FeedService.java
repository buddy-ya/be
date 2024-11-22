package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Category;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedUserAction;
import com.team.buddyya.feed.dto.request.feed.FeedCreateRequest;
import com.team.buddyya.feed.dto.request.feed.FeedListRequest;
import com.team.buddyya.feed.dto.request.feed.FeedUpdateRequest;
import com.team.buddyya.feed.dto.response.feed.FeedListResponse;
import com.team.buddyya.feed.dto.response.feed.FeedResponse;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.respository.BookmarkRepository;
import com.team.buddyya.feed.respository.FeedRepository;
import com.team.buddyya.feed.respository.LikeRepository;
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

    private final CategoryService categoryService;
    private final FeedRepository feedRepository;
    private final StudentRepository studentRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;

    public Feed findFeedByFeedId(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
    }

    public FeedListResponse getFeeds(StudentInfo studentInfo, FeedListRequest request) {
        Category category = categoryService.getCategory(request.category());
        PageRequest pageRequest = PageRequest.of(request.page(), request.size(),
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Feed> feeds = feedRepository.findAllByCategory(category, pageRequest);
        List<FeedResponse> response = feeds.getContent().stream()
                .map(feed -> createFeedResponse(feed, studentInfo.id()))
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

    private FeedResponse createFeedResponse(Feed feed, Long studentId) {
        FeedUserAction userAction = getUserAction(studentId, feed.getId());
        return FeedResponse.from(feed, userAction.isLiked(), userAction.isBookmarked());
    }

    private FeedUserAction getUserAction(Long studentId, Long feedId) {
        boolean isLiked = likeRepository.existsByStudentIdAndFeedId(studentId, feedId);
        boolean isBookmarked = bookmarkRepository.existsByStudentIdAndFeedId(studentId, feedId);
        return FeedUserAction.from(isLiked, isBookmarked);
    }
}
