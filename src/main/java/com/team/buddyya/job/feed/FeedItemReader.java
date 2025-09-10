package com.team.buddyya.job.feed;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.batch.item.ItemReader;
import org.springframework.jdbc.core.JdbcTemplate;

public class FeedItemReader implements ItemReader<FeedJobDTO> {

    private final JdbcTemplate jdbcTemplate;
    private final int totalCount;
    private final AtomicInteger counter = new AtomicInteger(0);

    private Long minStudentId;
    private Long maxStudentId;

    public FeedItemReader(JdbcTemplate jdbcTemplate, int totalCount) {
        this.jdbcTemplate = jdbcTemplate;
        this.totalCount = totalCount;
    }

    @Override
    public FeedJobDTO read() throws Exception {
        if (minStudentId == null) {
            if (jdbcTemplate.queryForObject("SELECT COUNT(1) FROM student", Long.class) == 0) {
                throw new IllegalStateException("Prerequisite data (Student) is missing.");
            }
            this.minStudentId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM student", Long.class);
            this.maxStudentId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM student", Long.class);
        }

        if (counter.get() >= totalCount) {
            return null;
        }

        int currentCount = counter.incrementAndGet();
        long randomStudentId = ThreadLocalRandom.current().nextLong(minStudentId, maxStudentId + 1);
        long universityId = 21L;
        Long categoryId = 1L;

        return FeedJobDTO.builder()
                .title("Feed")
                .content("" + currentCount)
                .profileVisible(ThreadLocalRandom.current().nextBoolean())
                .studentId(randomStudentId)
                .categoryId(categoryId)
                .universityId(universityId)
                .build();
    }
}
