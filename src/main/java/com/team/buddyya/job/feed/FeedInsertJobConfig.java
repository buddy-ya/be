package com.team.buddyya.job.feed;

import com.team.buddyya.feed.repository.CategoryRepository;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FeedInsertJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private final StudentRepository studentRepository;
    private final CategoryRepository categoryRepository;
    private final UniversityRepository universityRepository;

    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Step feedInsertStep(
            ItemReader<FeedJobDTO> feedItemReader,
            JdbcBatchItemWriter<FeedJobDTO> feedItemWriter
    ) {
        return new StepBuilder("feedInsertStep", jobRepository)
                .<FeedJobDTO, FeedJobDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(feedItemReader)
                .writer(feedItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<FeedJobDTO> feedItemReader(
            @Value("#{jobParameters['count'] ?: 100000}") int totalCount
    ) {
        return new FeedItemReader(studentRepository, categoryRepository, universityRepository, totalCount);
    }

    @Bean
    public JdbcBatchItemWriter<FeedJobDTO> feedItemWriter() {
        String sql =
                "INSERT INTO feed (title, content, is_profile_visible, student_id, category_id, university_id, like_count, comment_count, view_count, pinned, created_date, updated_date) "
                        +
                        "VALUES (:title, :content, :profileVisible, :studentId, :categoryId, :universityId, 0, 0, 0, false, NOW(), NOW())";
        return new JdbcBatchItemWriterBuilder<FeedJobDTO>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
