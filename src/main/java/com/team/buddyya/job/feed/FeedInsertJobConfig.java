package com.team.buddyya.job.feed;

import static com.team.buddyya.job.SeedDataJobConfig.FEED_COUNT;

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
public class FeedInsertJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Step feedInsertStep(ItemReader<FeedJobDTO> feedItemReader, JdbcBatchItemWriter<FeedJobDTO> feedItemWriter) {
        return new StepBuilder("feedInsertStep", jobRepository)
                .<FeedJobDTO, FeedJobDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(feedItemReader)
                .writer(feedItemWriter)
                .build();
    }

    @Bean
    public ItemReader<FeedJobDTO> feedItemReader() {
        return new FeedItemReader(new JdbcTemplate(dataSource), FEED_COUNT);
    }

    @Bean
    public JdbcBatchItemWriter<FeedJobDTO> feedItemWriter() {
        String sql = "INSERT INTO feed (title, content, is_profile_visible, student_id, category_id, university_id, like_count, comment_count, view_count, pinned, created_date, updated_date) VALUES (:title, :content, :profileVisible, :studentId, :categoryId, :universityId, 0, 0, 0, false, NOW(), NOW())";
        return new JdbcBatchItemWriterBuilder<FeedJobDTO>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
