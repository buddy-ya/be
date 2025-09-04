package com.team.buddyya.job.feed.comment;

import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.repository.FeedRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.repository.StudentRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.batch.item.ItemReader;

public class CommentItemReader implements ItemReader<CommentJobDTO> {

    private final FeedRepository feedRepository;
    private final StudentRepository studentRepository;
    private final int totalCount;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Random random = new Random();

    private List<Feed> feeds;
    private List<Student> students;

    public CommentItemReader(FeedRepository feedRepository, StudentRepository studentRepository, int totalCount) {
        this.feedRepository = feedRepository;
        this.studentRepository = studentRepository;
        this.totalCount = totalCount;
    }

    @Override
    public CommentJobDTO read() {
        if (feeds == null) {
            feeds = feedRepository.findAll();
            students = studentRepository.findAll();
            if (feeds.isEmpty() || students.isEmpty()) {
                throw new IllegalStateException("Prerequisite data (Feed or Student) is missing.");
            }
        }
        int currentCount = counter.getAndIncrement();
        if (currentCount >= totalCount) {
            return null;
        }
        Feed randomFeed = feeds.get(random.nextInt(feeds.size()));
        Student randomStudent = students.get(random.nextInt(students.size()));
        return CommentJobDTO.builder().studentId(randomStudent.getId()).feedId(randomFeed.getId())
                .content("Comment content " + (currentCount + 1)).parentId(null).build();
    }
}
