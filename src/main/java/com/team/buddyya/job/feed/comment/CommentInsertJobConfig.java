package com.team.buddyya.job.feed.comment;

import static com.team.buddyya.job.SeedDataJobConfig.COMMENT_COUNT;

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
public class CommentInsertJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Step commentInsertStep(ItemReader<CommentJobDTO> commentItemReader,
                                  JdbcBatchItemWriter<CommentJobDTO> commentItemWriter) {
        return new StepBuilder("commentInsertStep", jobRepository)
                .<CommentJobDTO, CommentJobDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(commentItemReader)
                .writer(commentItemWriter)
                .build();
    }

    @Bean
    public ItemReader<CommentJobDTO> commentItemReader() {
        return new CommentItemReader(new JdbcTemplate(dataSource), COMMENT_COUNT);
    }

    @Bean
    public JdbcBatchItemWriter<CommentJobDTO> commentItemWriter() {
        String sql = "INSERT INTO comment (student_id, feed_id, parent_id, content, like_count, deleted, created_date, updated_date) VALUES (:studentId, :feedId, :parentId, :content, 0, false, NOW(), NOW())";
        return new JdbcBatchItemWriterBuilder<CommentJobDTO>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
