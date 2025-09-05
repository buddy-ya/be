package com.team.buddyya.job.feed.like;

import static com.team.buddyya.job.SeedDataJobConfig.LIKE_COUNT;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FeedLikeInsertJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Step feedLikeInsertStep(ItemReader<FeedLikeJobDTO> feedLikeItemReader,
                                   JdbcBatchItemWriter<FeedLikeJobDTO> feedLikeItemWriter) {
        return new StepBuilder("feedLikeInsertStep", jobRepository)
                .<FeedLikeJobDTO, FeedLikeJobDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(feedLikeItemReader)
                .writer(feedLikeItemWriter)
                .build();
    }

    @Bean
    public ItemReader<FeedLikeJobDTO> feedLikeItemReader() {
        return new FeedLikeItemReader(new JdbcTemplate(dataSource), LIKE_COUNT);
    }

    @Bean
    public JdbcBatchItemWriter<FeedLikeJobDTO> feedLikeItemWriter() {
        String sql = "INSERT IGNORE INTO feed_like (feed_id, student_id, created_date) VALUES (:feedId, :studentId, NOW())";
        return new JdbcBatchItemWriterBuilder<FeedLikeJobDTO>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
