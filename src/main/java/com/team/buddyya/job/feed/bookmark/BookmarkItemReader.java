package com.team.buddyya.job.feed.bookmark;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.batch.item.ItemReader;
import org.springframework.jdbc.core.JdbcTemplate;

public class BookmarkItemReader implements ItemReader<BookmarkJobDTO> {

    private final JdbcTemplate jdbcTemplate;
    private final int totalCount;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private Long minFeedId;
    private Long maxFeedId;
    private Long minStudentId;
    private Long maxStudentId;

    public BookmarkItemReader(JdbcTemplate jdbcTemplate, int totalCount) {
        this.jdbcTemplate = jdbcTemplate;
        this.totalCount = totalCount;
    }

    @Override
    public BookmarkJobDTO read() {
        if (minFeedId == null) {
            this.minFeedId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM feed", Long.class);
            this.maxFeedId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM feed", Long.class);
            this.minStudentId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM student", Long.class);
            this.maxStudentId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM student", Long.class);
        }
        if (counter.getAndIncrement() >= totalCount) {
            return null;
        }
        long randomFeedId = random.nextLong(minFeedId, maxFeedId + 1);
        long randomStudentId = random.nextLong(minStudentId, maxStudentId + 1);
        return BookmarkJobDTO.builder()
                .feedId(randomFeedId)
                .studentId(randomStudentId)
                .build();
    }
}
