package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Bookmark;
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
import com.team.buddyya.student.service.FindStudentService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedService {

    private static final int LIKE_COUNT_THRESHOLD = 10;

    private final CategoryService categoryService;
    private final FeedRepository feedRepository;
    private final FindStudentService findStudentService;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FeedImageService feedImageService;

    private Feed findFeedByFeedId(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
    }

    private Student findStudentByStudentId(Long studentId) {
        return findStudentService.findByStudentId(studentId);
    }

    public FeedListResponse getFeeds(StudentInfo studentInfo, Pageable pageable, FeedListRequest request) {
        String keyword = request.keyword();
        if (keyword == null || keyword.isBlank()) {
            Page<Feed> feeds = getFeedsByCategory(request, pageable);
            List<FeedResponse> response = feeds.getContent().stream()
                    .map(feed -> createFeedResponse(feed, studentInfo.id()))
                    .toList();
            return FeedListResponse.from(response, feeds);
        }
        Page<Feed> feeds = getFeedsByKeyword(keyword, pageable);
        List<FeedResponse> response = feeds.getContent().stream()
                .map(feed -> createFeedResponse(feed, studentInfo.id()))
                .toList();
        return FeedListResponse.from(response, feeds);
    }

    public FeedListResponse getPopularFeeds(StudentInfo studentInfo, Pageable pageable) {
        Page<Feed> feeds = feedRepository.findByLikeCountGreaterThanEqual(LIKE_COUNT_THRESHOLD, pageable);
        List<FeedResponse> response = feeds.getContent().stream()
                .map(feed -> createFeedResponse(feed, studentInfo.id()))
                .toList();
        return FeedListResponse.from(response, feeds);
    }

    public FeedResponse getFeed(StudentInfo studentInfo, Long feedId) {
        Feed feed = findFeedByFeedId(feedId);
        return createFeedResponse(feed, studentInfo.id());
    }

    public void createFeed(StudentInfo studentInfo, FeedCreateRequest request) {
        Category category = categoryService.getCategory(request.category());
        Student student = findStudentByStudentId(studentInfo.id());
        Feed feed = Feed.builder()
                .title(request.title())
                .content(request.content())
                .student(student)
                .category(category)
                .university(student.getUniversity())
                .build();
        feedRepository.save(feed);
        uploadImages(feed, request.images());
    }

    public void updateFeed(StudentInfo studentInfo, Long feedId, FeedUpdateRequest request) {
        Feed feed = findFeedByFeedId(feedId);
        validateFeedOwner(studentInfo.id(), feed);
        Category category = categoryService.getCategory(request.category());
        feed.updateFeed(request.title(), request.content(), category);
        updateImages(feed, request.images());
    }

    public void deleteFeed(StudentInfo studentInfo, Long feedId) {
        Feed feed = findFeedByFeedId(feedId);
        validateFeedOwner(studentInfo.id(), feed);
        feedImageService.deleteS3FeedImages(feed);
        feedRepository.delete(feed);
    }

    private Page<Feed> getFeedsByCategory(FeedListRequest request, Pageable pageable) {
        Category category = categoryService.getCategory(request.category());
        Pageable customPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                getSortBy(category)
        );
        return feedRepository.findAllByCategoryName(category.getName(), customPageable);
    }

    private Page<Feed> getFeedsByKeyword(String keyword, Pageable pageable) {
        return feedRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }

    private void validateFeedOwner(Long studentId, Feed feed) {
        if (!studentId.equals(feed.getStudent().getId())) {
            throw new FeedException(FeedExceptionType.NOT_FEED_OWNER);
        }
    }

    private FeedResponse createFeedResponse(Feed feed, Long studentId) {
        Student student = findStudentByStudentId(studentId);
        FeedUserAction userAction = getUserAction(student, feed);
        return FeedResponse.from(feed, userAction);
    }

    private FeedUserAction getUserAction(Student student, Feed feed) {
        boolean isFeedOwner = student.getId().equals(feed.getStudent().getId());
        boolean isLiked = likeRepository.existsByStudentAndFeed(student, feed);
        boolean isBookmarked = bookmarkRepository.existsByStudentAndFeed(student, feed);
        return FeedUserAction.of(isFeedOwner, isLiked, isBookmarked);
    }

    private void updateImages(Feed feed, List<MultipartFile> images) {
        feedImageService.deleteFeedImages(feed);
        feedImageService.deleteS3FeedImages(feed);
        if (images != null && !images.isEmpty()) {
            List<FeedImage> feedImages = feedImageService.uploadS3FeedImages(feed, images);
            feedImageService.uploadFeedImages(feedImages);
        }
    }

    private void uploadImages(Feed feed, List<MultipartFile> images) {
        if (images != null && !images.isEmpty()) {
            List<FeedImage> feedImages = feedImageService.uploadS3FeedImages(feed, images);
            feed.uploadFeedImages(feedImages);
        }
    }

    private Sort getSortBy(Category category) {
        return category.getName().equals("POPULAR")
                ? Sort.by(Sort.Direction.DESC, "likeCount", "createdDate")
                : Sort.by(Sort.Direction.DESC, "createdDate");
    }

    public FeedListResponse getMyFeed(StudentInfo studentInfo, Pageable pageable) {
        Pageable customPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
        Student student = findStudentByStudentId(studentInfo.id());
        Page<Feed> feeds = feedRepository.findAllByStudent(student, customPageable);
        List<FeedResponse> response = feeds.getContent().stream()
                .map(feed -> createFeedResponse(feed, studentInfo.id()))
                .toList();
        return FeedListResponse.from(response, feeds);
    }

    public FeedListResponse getBookmarkFeed(StudentInfo studentInfo, Pageable pageable) {
        Student student = findStudentByStudentId(studentInfo.id());
        Page<Bookmark> bookmarks = bookmarkRepository.findAllByStudent(student, pageable);
        Page<Feed> feeds = bookmarks.map(Bookmark::getFeed);
        List<FeedResponse> response = bookmarks.getContent().stream()
                .map(bookmark -> createFeedResponse(bookmark.getFeed(), studentInfo.id()))
                .toList();
        return FeedListResponse.from(response, feeds);
    }
}
