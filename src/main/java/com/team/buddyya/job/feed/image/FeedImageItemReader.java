package com.team.buddyya.job.feed.image;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.batch.item.ItemReader;
import org.springframework.jdbc.core.JdbcTemplate;

public class FeedImageItemReader implements ItemReader<FeedImageJobDTO> {

    private final JdbcTemplate jdbcTemplate;
    private final int totalFeeds;
    private final AtomicInteger feedCounter = new AtomicInteger(0);
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final List<FeedImageJobDTO> imageBuffer = new ArrayList<>();

    private Long minFeedId;
    private Long maxFeedId;

    public FeedImageItemReader(JdbcTemplate jdbcTemplate, int totalFeeds) {
        this.jdbcTemplate = jdbcTemplate;
        this.totalFeeds = totalFeeds;
    }

    @Override
    public FeedImageJobDTO read() {
        if (minFeedId == null) {
            this.minFeedId = jdbcTemplate.queryForObject("SELECT MIN(id) FROM feed", Long.class);
            this.maxFeedId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM feed", Long.class);
        }
        if (!imageBuffer.isEmpty()) {
            return imageBuffer.remove(0);
        }
        while (feedCounter.get() < totalFeeds) {
            feedCounter.incrementAndGet();
            if (random.nextInt(10) < 3) {
                long randomFeedId = random.nextLong(minFeedId, maxFeedId + 1);
                int imageCount = random.nextInt(1, 3);

                for (int i = 0; i < imageCount; i++) {
                    int imageIndex = random.nextInt(1, 9);
                    String imageUrl = String.format(
                            "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image__%d.png",
                            imageIndex);
                    imageBuffer.add(FeedImageJobDTO.builder().feedId(randomFeedId).url(imageUrl).build());
                }
                return imageBuffer.remove(0);
            }
        }
        return null;
    }
}
