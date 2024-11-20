package com.team.buddyya.feed.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.feed.domain.Category;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.dto.request.FeedListRequest;
import com.team.buddyya.feed.dto.response.FeedListItemResponse;
import com.team.buddyya.feed.dto.response.FeedListResponse;
import com.team.buddyya.feed.respository.FeedRepository;
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

    private final FeedRepository feedRepository;
    private final LikeSevice likeSevice;
    private final BookmarkService bookmarkService;
    private final CategoryService categoryService;

    public FeedListResponse getFeeds(StudentInfo studentInfo, FeedListRequest request) {
        Category category = categoryService.getCategory(request.category());
        PageRequest pageRequest = PageRequest.of(request.page(), request.size(),
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Feed> feeds = feedRepository.findAllByCategory(category, pageRequest);
        List<FeedListItemResponse> feedResponses = feeds.getContent().stream()
                .map(feed -> createFeedListItemResponse(feed, studentInfo))
                .toList();
        return FeedListResponse.from(feeds, feedResponses);
    }

    private FeedListItemResponse createFeedListItemResponse(Feed feed, StudentInfo studentInfo) {
        Long feedId = feed.getId();
        Long studentId = studentInfo.id();
        boolean isLiked = likeSevice.isLikedByStudent(studentId, feedId);
        boolean isBookmarked = bookmarkService.isBookmarkedByStudent(studentId, feedId);
        return FeedListItemResponse.from(feed, isLiked, isBookmarked);
    }
}
