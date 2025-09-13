package com.team.buddyya.feed.repository;

import static com.team.buddyya.feed.domain.QFeed.feed;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.dto.request.feed.FeedCursorListRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Feed> findFeedsByCursor(FeedCursorListRequest request, Feed lastFeed) {
        int pageSize = (request.pageSize() != null && request.pageSize() > 0) ? request.pageSize() : DEFAULT_PAGE_SIZE;
        List<Feed> feeds = queryFactory
                .selectFrom(feed)
                .where(
                        cursorCondition(lastFeed),
                        eqUniversity(request.university()),
                        eqCategory(request.category()),
                        containsKeyword(request.keyword())
                )
                .orderBy(feed.pinned.desc(), feed.createdDate.desc(), feed.id.desc())
                .limit(pageSize + 1)
                .fetch();
        return toSlice(feeds, pageSize);
    }

    private BooleanExpression cursorCondition(Feed lastFeed) {
        if (lastFeed == null) {
            return null;
        }
        BooleanExpression pinnedExpression = feed.pinned.coalesce(false);
        boolean lastPinned = lastFeed.isPinned();
        LocalDateTime lastCreatedAt = lastFeed.getCreatedDate();
        Long lastId = lastFeed.getId();
        return pinnedExpression.lt(lastPinned)
                .or(pinnedExpression.eq(lastPinned)
                        .and(feed.createdDate.lt(lastCreatedAt)))
                .or(pinnedExpression.eq(lastPinned)
                        .and(feed.createdDate.eq(lastCreatedAt))
                        .and(feed.id.lt(lastId)));
    }

    private BooleanExpression eqUniversity(String university) {
        if (university == null || university.isBlank() || university.equalsIgnoreCase("all")) {
            return null;
        }
        return feed.university.universityName.eq(university);
    }

    private BooleanExpression eqCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }
        return feed.category.name.eq(category);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return feed.title.containsIgnoreCase(keyword)
                .or(feed.content.containsIgnoreCase(keyword));
    }

    private Slice<Feed> toSlice(List<Feed> feeds, int pageSize) {
        boolean hasNext = feeds.size() > pageSize;
        if (hasNext) {
            feeds.remove(pageSize);
        }
        return new SliceImpl<>(feeds, PageRequest.of(0, pageSize), hasNext);
    }
}
