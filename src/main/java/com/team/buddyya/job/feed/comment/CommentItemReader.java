package com.team.buddyya.job.feed.comment;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.batch.item.ItemReader;
import org.springframework.jdbc.core.JdbcTemplate;

public class CommentItemReader implements ItemReader<CommentJobDTO> {

    private final JdbcTemplate jdbcTemplate;
    private final int totalCount;
    private final AtomicInteger counter = new AtomicInteger(0);

    private Long minStudentId;
    private Long maxStudentId;
    private Long minFeedId;
    private Long maxFeedId;

    public CommentItemReader(JdbcTemplate jdbcTemplate, int totalCount) {
        this.jdbcTemplate = jdbcTemplate;
        this.totalCount = totalCount;
    }

    @Override
    public CommentJobDTO read() throws Exception {
        if (minFeedId == null) {
            this.minStudentId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM student", Long.class);
            this.maxStudentId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM student", Long.class);
            this.minFeedId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM feed", Long.class);
            this.maxFeedId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM feed", Long.class);
        }
        if (counter.get() >= totalCount) {
            return null;
        }
        int currentCount = counter.incrementAndGet();
        long randomStudentId = ThreadLocalRandom.current().nextLong(minStudentId, maxStudentId + 1);
        long randomFeedId = ThreadLocalRandom.current().nextLong(minFeedId, maxFeedId + 1);
        return CommentJobDTO.builder()
                .studentId(randomStudentId)
                .feedId(randomFeedId)
                .content("Comment content " + currentCount)
                .parentId(null)
                .build();
    }
}
