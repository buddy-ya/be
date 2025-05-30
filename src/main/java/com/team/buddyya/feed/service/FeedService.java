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
import com.team.buddyya.feed.repository.BookmarkRepository;
import com.team.buddyya.feed.repository.FeedLikeRepository;
import com.team.buddyya.feed.repository.FeedRepository;
import com.team.buddyya.report.domain.ReportType;
import com.team.buddyya.report.repository.ReportRepository;
import com.team.buddyya.student.domain.Role;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.BlockRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import com.team.buddyya.student.service.FindStudentService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedService {

    private static final int LIKE_COUNT_THRESHOLD = 10;

    private final CategoryService categoryService;
    private final FeedRepository feedRepository;
    private final FindStudentService findStudentService;
    private final FeedLikeRepository feedLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FeedImageService feedImageService;
    private final BlockRepository blockRepository;
    private final UniversityRepository universityRepository;
    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    protected Feed findFeedByFeedId(Long feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    protected Student findStudentByStudentId(Long studentId) {
        return findStudentService.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    protected University findUniversityByUniversityName(String universityName) {
        return universityRepository.findByUniversityName(universityName)
                .orElseThrow(() -> new StudentException(StudentExceptionType.UNIVERSITY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public FeedListResponse getFeeds(StudentInfo studentInfo, Pageable pageable, FeedListRequest request) {
        String keyword = request.keyword();
        Student student = findStudentByStudentId(studentInfo.id());
        Page<Feed> feeds = (keyword == null || keyword.isBlank())
                ? getFeedsByUniversityAndCategory(request, pageable)
                : getFeedsByKeyword(student, keyword, pageable);
        Set<Long> blockedStudentIds = blockRepository.findBlockedStudentIdByBlockerId(studentInfo.id());
        List<FeedResponse> response = filterBlockedFeeds(feeds.getContent(), blockedStudentIds, studentInfo.id());
        return FeedListResponse.from(response, feeds);
    }

    @Transactional(readOnly = true)
    public FeedListResponse getPopularFeeds(
            StudentInfo studentInfo,
            Pageable pageable,
            FeedListRequest request
    ) {
        University university = findUniversityByUniversityName(request.university());
        Page<Feed> feeds = feedRepository
                .findByLikeCountGreaterThanEqualAndUniversity(
                        LIKE_COUNT_THRESHOLD,
                        university,
                        pageable
                );
        Set<Long> blocked = blockRepository.findBlockedStudentIdByBlockerId(studentInfo.id());
        List<FeedResponse> response = filterBlockedFeeds(feeds.getContent(), blocked, studentInfo.id());
        return FeedListResponse.from(response, feeds);
    }

    @Transactional(readOnly = true)
    Page<Feed> getFeedsByUniversityAndCategory(FeedListRequest request, Pageable pageable) {
        University university = findUniversityByUniversityName(request.university());
        Category category = categoryService.getCategory(request.category());
        Pageable customPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                getSortBy(category)
        );
        return feedRepository.findAllByUniversityAndCategory(university, category, customPageable);
    }

    @Transactional(readOnly = true)
    public FeedListResponse getMyFeed(StudentInfo studentInfo, Pageable pageable) {
        Pageable customPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdDate")
        );
        Student student = findStudentByStudentId(studentInfo.id());
        Page<Feed> feeds = feedRepository.findAllByStudent(student, customPageable);
        List<FeedResponse> response = feeds.getContent().stream()
                .map(feed -> createFeedResponse(feed, studentInfo.id()))
                .toList();
        return FeedListResponse.from(response, feeds);
    }

    @Transactional(readOnly = true)
    public FeedListResponse getBookmarkFeed(StudentInfo studentInfo, Pageable pageable) {
        Student student = findStudentByStudentId(studentInfo.id());
        Page<Bookmark> bookmarks = bookmarkRepository.findAllByStudent(student, pageable);
        Page<Feed> feeds = bookmarks.map(Bookmark::getFeed);
        Set<Long> blockedStudentIds = blockRepository.findBlockedStudentIdByBlockerId(studentInfo.id());
        List<FeedResponse> response = filterBlockedFeeds(feeds.getContent(), blockedStudentIds, studentInfo.id());
        return FeedListResponse.from(response, feeds);
    }

    @Transactional(readOnly = true)
    Page<Feed> getFeedsByKeyword(Student student, String keyword, Pageable pageable) {
        University openUniversity = findUniversityByUniversityName("all");
        List<University> universities = List.of(student.getUniversity(), openUniversity);
        return feedRepository.findByTitleContainingOrContentContainingAndUniversityIn(
                keyword, keyword, universities, pageable);
    }

    @Transactional(readOnly = true)
    FeedUserAction getUserAction(Student student, Feed feed) {
        boolean isFeedOwner = student.getId().equals(feed.getStudent().getId());
        boolean isLiked = feedLikeRepository.existsByStudentAndFeed(student, feed);
        boolean isBookmarked = bookmarkRepository.existsByStudentAndFeed(student, feed);
        return FeedUserAction.from(isFeedOwner, isLiked, isBookmarked);
    }

    public FeedResponse getFeed(StudentInfo studentInfo, Long feedId) {
        Feed feed = findFeedByFeedId(feedId);
        feed.increaseViewCount();
        return createFeedResponse(feed, studentInfo.id());
    }

    public void createFeed(StudentInfo studentInfo, FeedCreateRequest request) {
        Category category = categoryService.getCategory(request.category());
        University university = universityRepository.findByUniversityName(request.university())
                .orElseThrow(() -> new StudentException(StudentExceptionType.UNIVERSITY_NOT_FOUND));
        Student student = findStudentByStudentId(studentInfo.id());
        Feed feed = Feed.builder()
                .title(request.title())
                .content(request.content())
                .student(student)
                .category(category)
                .university(university)
                .isProfileVisible(request.isProfileVisible())
                .build();
        feedRepository.save(feed);
        uploadImages(feed, request.images());
    }

    public void updateFeed(StudentInfo studentInfo, Long feedId, FeedUpdateRequest request) {
        Feed feed = findFeedByFeedId(feedId);
        validateFeedOwner(studentInfo, feed);
        Category category = categoryService.getCategory(request.category());
        feed.updateFeed(request.title(), request.content(), category, request.isProfileVisible());
        updateImages(feed, request.images());
    }

    public void deleteFeed(StudentInfo studentInfo, Long feedId) {
        Feed feed = findFeedByFeedId(feedId);
        validateFeedOwner(studentInfo, feed);
        boolean isReportedFeed = reportRepository.existsByTypeAndReportedId(ReportType.FEED, feed.getId());
        if (!isReportedFeed) {
            feedImageService.deleteS3FeedImages(feed);
        }
        feedRepository.delete(feed);
    }

    public void togglePin(StudentInfo studentInfo, Long feedId) {
        Feed feed = findFeedByFeedId(feedId);
        if (studentInfo.role() != Role.OWNER) {
            throw new FeedException(FeedExceptionType.STUDENT_NOT_OWNER);
        }
        feed.togglePin();
    }

    private List<FeedResponse> filterBlockedFeeds(List<Feed> feeds, Set<Long> blockedStudentIds,
                                                  Long currentStudentId) {
        return feeds.stream()
                .filter(feed -> !blockedStudentIds.contains(feed.getStudent().getId()))
                .map(feed -> createFeedResponse(feed, currentStudentId))
                .toList();
    }

    private void validateFeedOwner(StudentInfo studentInfo, Feed feed) {
        if (!studentInfo.id().equals(feed.getStudent().getId()) && !(studentInfo.role() == Role.OWNER)) {
            throw new FeedException(FeedExceptionType.NOT_FEED_OWNER);
        }
    }

    private FeedResponse createFeedResponse(Feed feed, Long studentId) {
        Student student = findStudentByStudentId(studentId);
        FeedUserAction userAction = getUserAction(student, feed);
        return FeedResponse.from(feed, userAction);
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
                : Sort.by(Sort.Direction.DESC, "pinned", "createdDate");
    }
}
