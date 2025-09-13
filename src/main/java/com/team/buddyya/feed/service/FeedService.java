package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Bookmark;
import com.team.buddyya.feed.domain.Category;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedImage;
import com.team.buddyya.feed.domain.FeedUserAction;
import com.team.buddyya.feed.dto.projection.FeedAuthorInfo;
import com.team.buddyya.feed.dto.request.feed.FeedCreateRequest;
import com.team.buddyya.feed.dto.request.feed.FeedCursorListRequest;
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
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import com.team.buddyya.student.service.FindStudentService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
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
    private final StudentRepository studentRepository;

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
        Student student = findStudentByStudentId(studentInfo.id());
        Page<Feed> feeds = (request.keyword() == null || request.keyword().isBlank())
                ? getFeedsByUniversityAndCategory(request, pageable)
                : getFeedsByKeyword(student, request.keyword(), pageable);
        return createFeedListResponse(feeds, studentInfo);
    }

    @Transactional(readOnly = true)
    public FeedListResponse getFeedsByCursor(StudentInfo studentInfo, FeedCursorListRequest request) {
        final Feed lastFeed = request.lastId() != null ?
                feedRepository.findById(request.lastId())
                        .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND))
                : null;
        Slice<Feed> feedSlice = feedRepository.findFeedsByCursor(request, lastFeed);
        return createFeedListResponse(feedSlice, studentInfo);
    }


    @Transactional(readOnly = true)
    public FeedListResponse getPopularFeeds(
            StudentInfo studentInfo,
            Pageable pageable,
            FeedListRequest request
    ) {
        University university = findUniversityByUniversityName(request.university());
        Page<Feed> feeds = feedRepository.findByLikeCountGreaterThanEqualAndUniversity(LIKE_COUNT_THRESHOLD, university,
                pageable);
        return createFeedListResponse(feeds, studentInfo);
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
                .map(feed -> createFeedResponse(feed, student))
                .toList();
        return FeedListResponse.from(response, feeds);
    }

    @Transactional(readOnly = true)
    public FeedListResponse getBookmarkFeed(StudentInfo studentInfo, Pageable pageable) {
        Student student = findStudentByStudentId(studentInfo.id());
        Page<Bookmark> bookmarks = bookmarkRepository.findAllByStudent(student, pageable);
        Page<Feed> feeds = bookmarks.map(Bookmark::getFeed);
        return createFeedListResponse(feeds, studentInfo);
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
        Student student = findStudentByStudentId(studentInfo.id());
        Feed feed = findFeedByFeedId(feedId);
        feed.increaseViewCount();
        FeedResponse response = createFeedResponse(feed, student);
        return response;
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

    private FeedListResponse createFeedListResponse(Slice<Feed> feedSlice, StudentInfo studentInfo) {
        List<Feed> feeds = feedSlice.getContent();
        Student student = findStudentByStudentId(studentInfo.id());
        if (feeds.isEmpty()) {
            return FeedListResponse.from(List.of(), feedSlice);
        }
        Set<Long> blockedStudentIds = blockRepository.findBlockedStudentIdByBlockerId(studentInfo.id());
        List<Long> feedIds = feeds.stream().map(Feed::getId).toList();
        Set<Long> likedFeedIds = feedLikeRepository.findFeedIdsByStudentIdAndFeedIdsIn(studentInfo.id(), feedIds);
        Set<Long> bookmarkedFeedIds = bookmarkRepository.findFeedIdsByStudentIdAndFeedIdsIn(studentInfo.id(), feedIds);
        Map<Long, FeedAuthorInfo> authorInfoMap = getFeedAuthorInfoMap(feeds);
        List<FeedResponse> response = filterBlockedFeeds(
                feeds,
                blockedStudentIds,
                student,
                likedFeedIds,
                bookmarkedFeedIds,
                authorInfoMap
        );
        return FeedListResponse.from(response, feedSlice);
    }

    private List<FeedResponse> filterBlockedFeeds(List<Feed> feeds, Set<Long> blockedStudentIds,
                                                  Student currentStudent, Set<Long> likedFeedIds,
                                                  Set<Long> bookmarkedFeedIds,
                                                  Map<Long, FeedAuthorInfo> authorInfoMap) {
        return feeds.stream()
                .filter(feed -> !blockedStudentIds.contains(feed.getStudent().getId()))
                .map(feed -> {
                    boolean isFeedOwner = feed.isFeedOwner(currentStudent.getId());
                    boolean isLiked = likedFeedIds.contains(feed.getId());
                    boolean isBookmarked = bookmarkedFeedIds.contains(feed.getId());
                    FeedUserAction userAction = FeedUserAction.from(isFeedOwner, isLiked, isBookmarked);
                    FeedAuthorInfo authorInfo = authorInfoMap.get(feed.getStudent().getId());
                    return FeedResponse.from(feed, userAction, authorInfo);
                })
                .toList();
    }

    private Map<Long, FeedAuthorInfo> getFeedAuthorInfoMap(List<Feed> feeds) {
        Set<Long> studentIds = feeds.stream()
                .map(feed -> feed.getStudent().getId())
                .collect(Collectors.toSet());
        if (studentIds.isEmpty()) {
            return Map.of();
        }
        return studentRepository.findAuthorInfoByIdsIn(studentIds).stream()
                .collect(Collectors.toMap(FeedAuthorInfo::id, Function.identity()));
    }


    private void validateFeedOwner(StudentInfo studentInfo, Feed feed) {
        if (!studentInfo.id().equals(feed.getStudent().getId()) && !(studentInfo.role() == Role.OWNER)) {
            throw new FeedException(FeedExceptionType.NOT_FEED_OWNER);
        }
    }

    private FeedResponse createFeedResponse(Feed feed, Student currentStudent) {
        FeedUserAction userAction = getUserAction(currentStudent, feed);
        Student authorStudent = feed.getStudent();
        FeedAuthorInfo authorInfo = new FeedAuthorInfo(
                authorStudent.getId(),
                authorStudent.getName(),
                authorStudent.getCountry(),
                authorStudent.getRole(),
                authorStudent.getCharacterProfileImage(),
                authorStudent.getIsCertificated(),
                authorStudent.getIsDeleted(),
                authorStudent.getUniversity().getUniversityName()
        );
        return FeedResponse.from(feed, userAction, authorInfo);
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
