package com.team.buddyya.job;

import com.team.buddyya.job.feed.FeedInsertJobConfig;
import com.team.buddyya.job.student.StudentInsertJobConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Import({StudentInsertJobConfig.class, FeedInsertJobConfig.class})
@Configuration
@RequiredArgsConstructor
public class SeedDataJobConfig {

    private final JobRepository jobRepository;
    private final Step studentInsertStep;
    private final Step feedInsertStep;

    @Bean
    public Job seedDataJob() {
        return new JobBuilder("seedDataJob", jobRepository)
                .start(studentInsertStep)
                .next(feedInsertStep)
                .build();
    }
}
