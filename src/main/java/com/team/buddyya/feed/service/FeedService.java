package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Category;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedImage;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedService {

    private final CategoryService categoryService;
    private final FeedRepository feedRepository;
    private final StudentRepository studentRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FeedImageService feedImageService;

    public Feed findFeedByFeedId(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
    }

    public FeedListResponse getFeeds(StudentInfo studentInfo, Pageable pageable, FeedListRequest request) {
        Category category = categoryService.getCategory(request.category());
        Page<Feed> feeds = feedRepository.findAllByCategoryName(category.getName(), pageable);
        List<FeedResponse> response = feeds.getContent().stream()
                .map(feed -> createFeedResponse(feed, studentInfo.id()))
                .toList();
        return FeedListResponse.from(response, feeds);
    }

    public FeedResponse getFeed(StudentInfo studentInfo, Long feedId) {
        Feed feed = findFeedByFeedId(feedId);
        return createFeedResponse(feed, studentInfo.id());
    }

    public void createFeed(StudentInfo studentInfo, FeedCreateRequest request, List<MultipartFile> images) {
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
        uploadImages(feed, images);
    }

    public void updateFeed(StudentInfo studentInfo, Long feedId, FeedUpdateRequest request,
                           List<MultipartFile> images) {
        Feed feed = findFeedByFeedId(feedId);
        validateFeedOwner(studentInfo.id(), feed);
        Category category = categoryService.getCategory(request.category());
        feed.updateFeed(request.title(), request.content(), category);
        uploadImages(feed, images);
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
        return FeedResponse.from(feed, userAction);
    }

    private FeedUserAction getUserAction(Long studentId, Long feedId) {
        boolean isFeedOwner = studentId.equals(feedId);
        boolean isLiked = likeRepository.existsByStudentIdAndFeedId(studentId, feedId);
        boolean isBookmarked = bookmarkRepository.existsByStudentIdAndFeedId(studentId, feedId);
        return FeedUserAction.of(isFeedOwner, isLiked, isBookmarked);
    }

    private void uploadImages(Feed feed, List<MultipartFile> images) {
        if (images != null && !images.isEmpty()) {
            List<FeedImage> feedImages = feedImageService.uploadFeedImages(feed, images);
            feedImages.forEach(feed::uploadFeedImage);
        }
    }
}
