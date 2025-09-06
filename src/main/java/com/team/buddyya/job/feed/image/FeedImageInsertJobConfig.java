package com.team.buddyya.job.feed.image;

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
public class FeedImageInsertJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Step feedImageInsertStep(ItemReader<FeedImageJobDTO> feedImageItemReader,
                                    JdbcBatchItemWriter<FeedImageJobDTO> feedImageItemWriter) {
        return new StepBuilder("feedImageInsertStep", jobRepository)
                .<FeedImageJobDTO, FeedImageJobDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(feedImageItemReader)
                .writer(feedImageItemWriter)
                .build();
    }

    @Bean
    public ItemReader<FeedImageJobDTO> feedImageItemReader() {
        return new FeedImageItemReader(new JdbcTemplate(dataSource), FEED_COUNT);
    }

    @Bean
    public JdbcBatchItemWriter<FeedImageJobDTO> feedImageItemWriter() {
        String sql = "INSERT INTO feed_image (feed_id, url, created_date) VALUES (:feedId, :url, NOW())";
        return new JdbcBatchItemWriterBuilder<FeedImageJobDTO>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
