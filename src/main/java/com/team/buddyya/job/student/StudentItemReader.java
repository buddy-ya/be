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
        int profileImageIndex = ThreadLocalRandom.current().nextInt(1, 9);
        String profileImageUrl = String.format(
                "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image__%d.png",
                profileImageIndex);
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
