package com.team.buddyya.job.feed.bookmark;

import static com.team.buddyya.job.SeedDataJobConfig.BOOKMARK_COUNT;

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
public class BookmarkInsertJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Step bookmarkInsertStep(ItemReader<BookmarkJobDTO> bookmarkItemReader,
                                   JdbcBatchItemWriter<BookmarkJobDTO> bookmarkItemWriter) {
        return new StepBuilder("bookmarkInsertStep", jobRepository)
                .<BookmarkJobDTO, BookmarkJobDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(bookmarkItemReader)
                .writer(bookmarkItemWriter)
                .build();
    }

    @Bean
    public ItemReader<BookmarkJobDTO> bookmarkItemReader() {
        return new BookmarkItemReader(new JdbcTemplate(dataSource), BOOKMARK_COUNT);
    }

    @Bean
    public JdbcBatchItemWriter<BookmarkJobDTO> bookmarkItemWriter() {
        String sql = "INSERT IGNORE INTO bookmark (feed_id, student_id, created_date) VALUES (:feedId, :studentId, NOW())";
        return new JdbcBatchItemWriterBuilder<BookmarkJobDTO>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .assertUpdates(false)
                .build();
    }
}
