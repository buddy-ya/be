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
    private Long minUniversityId;
    private Long maxUniversityId;

    public FeedItemReader(JdbcTemplate jdbcTemplate, int totalCount) {
        this.jdbcTemplate = jdbcTemplate;
        this.totalCount = totalCount;
    }

    @Override
    public FeedJobDTO read() throws Exception {
        if (minStudentId == null) {
            // Student, University 테이블의 데이터 존재 여부 및 ID 범위 초기화
            if (jdbcTemplate.queryForObject("SELECT COUNT(1) FROM student", Long.class) == 0) {
                throw new IllegalStateException("Prerequisite data (Student) is missing.");
            }
            this.minStudentId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM student", Long.class);
            this.maxStudentId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM student", Long.class);
            if (jdbcTemplate.queryForObject("SELECT COUNT(1) FROM university", Long.class) == 0) {
                throw new IllegalStateException("Prerequisite data (University) is missing.");
            }
            this.minUniversityId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM university", Long.class);
            this.maxUniversityId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM university", Long.class);
        }

        if (counter.get() >= totalCount) {
            return null;
        }

        int currentCount = counter.incrementAndGet();
        long randomStudentId = ThreadLocalRandom.current().nextLong(minStudentId, maxStudentId + 1);
        long randomUniversityId = ThreadLocalRandom.current().nextLong(minUniversityId, maxUniversityId + 1);
        // Category는 데이터가 적다고 가정하고 SQL로 랜덤 조회, 많아진다면 동일하게 MIN/MAX 방식으로 변경
        Long randomCategoryId = jdbcTemplate.queryForObject("SELECT id FROM category ORDER BY RAND() LIMIT 1",
                Long.class);

        return FeedJobDTO.builder()
                .title("피드 제목 " + currentCount)
                .content("피드 내용입니다. " + currentCount)
                .profileVisible(ThreadLocalRandom.current().nextBoolean())
                .studentId(randomStudentId)
                .categoryId(randomCategoryId)
                .universityId(randomUniversityId)
                .build();
    }
}
