package com.team.buddyya.job.student;

import static com.team.buddyya.job.SeedDataJobConfig.STUDENT_COUNT;

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
public class StudentInsertJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Step studentInsertStep(ItemReader<StudentJobDTO> studentItemReader,
                                  JdbcBatchItemWriter<StudentJobDTO> studentItemWriter) {
        return new StepBuilder("studentInsertStep", jobRepository)
                .<StudentJobDTO, StudentJobDTO>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(studentItemReader)
                .writer(studentItemWriter)
                .build();
    }

    @Bean
    public ItemReader<StudentJobDTO> studentItemReader() {
        return new StudentItemReader(new JdbcTemplate(dataSource), STUDENT_COUNT);
    }

    @Bean
    public JdbcBatchItemWriter<StudentJobDTO> studentItemWriter() {
        String sql = "INSERT INTO student (phone_number, name, country, certificated, korean, deleted, university_id, role, gender, character_profile_image, banned, created_date, updated_date) VALUES (:phoneNumber, :name, :country, :isCertificated, :isKorean, :isDeleted, :universityId, :role, :gender, :characterProfileImage, :isBanned, NOW(), NOW())";
        return new JdbcBatchItemWriterBuilder<StudentJobDTO>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
