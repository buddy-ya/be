package com.team.buddyya.job.feed.comment;

import com.team.buddyya.feed.repository.FeedRepository;
import com.team.buddyya.student.repository.StudentRepository;
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
public class CommentInsertJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private final FeedRepository feedRepository;
    private final StudentRepository studentRepository;
    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Step commentInsertStep(ItemReader<CommentJobDTO> commentItemReader,
                                  JdbcBatchItemWriter<CommentJobDTO> commentItemWriter) {
        return new StepBuilder("commentInsertStep", jobRepository).<CommentJobDTO, CommentJobDTO>chunk(CHUNK_SIZE,
                platformTransactionManager).reader(commentItemReader).writer(commentItemWriter).build();
    }

    @Bean
    @StepScope
    public ItemReader<CommentJobDTO> commentItemReader(@Value("#{jobParameters['count'] ?: 100000}") int totalCount) {
        return new CommentItemReader(feedRepository, studentRepository, totalCount);
    }

    @Bean
    public JdbcBatchItemWriter<CommentJobDTO> commentItemWriter() {
        String sql = "INSERT INTO comment (student_id, feed_id, parent_id, content, like_count, deleted, created_date, updated_date) VALUES (:studentId, :feedId, :parentId, :content, 0, false, NOW(), NOW())";
        return new JdbcBatchItemWriterBuilder<CommentJobDTO>().dataSource(dataSource).sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>()).build();
    }
}
