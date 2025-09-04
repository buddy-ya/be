package com.team.buddyya.job.student;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.batch.item.ItemReader;
import org.springframework.jdbc.core.JdbcTemplate;

public class StudentItemReader implements ItemReader<StudentJobDTO> {

    private final JdbcTemplate jdbcTemplate;
    private final int totalCount;
    private final AtomicInteger counter = new AtomicInteger(0);

    private Long minUniversityId;
    private Long maxUniversityId;

    public StudentItemReader(JdbcTemplate jdbcTemplate, int totalCount) {
        this.jdbcTemplate = jdbcTemplate;
        this.totalCount = totalCount;
    }

    @Override
    public StudentJobDTO read() {
        if (minUniversityId == null) {
            // University 테이블의 데이터 존재 여부 및 ID 범위 초기화
            if (jdbcTemplate.queryForObject("SELECT COUNT(1) FROM university", Long.class) == 0) {
                throw new IllegalStateException("Prerequisite data (University) is missing.");
            }
            this.minUniversityId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM university", Long.class);
            this.maxUniversityId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM university", Long.class);
        }

        if (counter.get() >= totalCount) {
            return null;
        }

        int index = counter.incrementAndGet();
        long randomUniversityId = ThreadLocalRandom.current().nextLong(minUniversityId, maxUniversityId + 1);
        String gender = (index % 2 == 0) ? "MALE" : "FEMALE";

        return StudentJobDTO.builder()
                .phoneNumber("010" + String.format("%08d", index))
                .name("student" + index)
                .country("ko")
                .isCertificated(false)
                .isKorean(true)
                .isDeleted(false)
                .universityId(randomUniversityId)
                .role("STUDENT")
                .gender(gender)
                .characterProfileImage("default_image_url_" + (index % 8))
                .isBanned(false)
                .build();
    }
}
