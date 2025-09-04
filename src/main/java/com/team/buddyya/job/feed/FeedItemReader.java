package com.team.buddyya.job.feed;

import com.team.buddyya.feed.domain.Category;
import com.team.buddyya.feed.repository.CategoryRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

@Slf4j
public class FeedItemReader implements ItemReader<FeedJobDTO> {

    private final StudentRepository studentRepository;
    private final CategoryRepository categoryRepository;
    private final UniversityRepository universityRepository;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final int totalCount;
    private final Random random = new Random();

    private List<Student> students;
    private List<Category> categories;
    private List<University> universities;

    public FeedItemReader(StudentRepository studentRepository, CategoryRepository categoryRepository,
                          UniversityRepository universityRepository, int totalCount) {
        this.studentRepository = studentRepository;
        this.categoryRepository = categoryRepository;
        this.universityRepository = universityRepository;
        this.totalCount = totalCount;
    }

    @Override
    public FeedJobDTO read() {
        if (students == null) {
            students = studentRepository.findAll();
            categories = categoryRepository.findAll();
            universities = universityRepository.findAll();
            if (students.isEmpty() || categories.isEmpty() || universities.isEmpty()) {
                throw new IllegalStateException("Prerequisite data (Student, Category, or University) is missing.");
            }
        }
        int currentCount = counter.getAndIncrement();
        if (currentCount >= totalCount) {
            return null;
        }
        Student randomStudent = students.get(random.nextInt(students.size()));
        Category randomCategory = categories.get(random.nextInt(categories.size()));
        University randomUniversity = universities.get(random.nextInt(universities.size()));
        return FeedJobDTO.builder()
                .title("피드 제목 " + (currentCount + 1))
                .content("피드 내용입니다. " + (currentCount + 1) + "번째 글입니다. 무작위 텍스트...")
                .isProfileVisible(random.nextBoolean())
                .studentId(randomStudent.getId())
                .categoryId(randomCategory.getId())
                .universityId(randomUniversity.getId())
                .build();
    }
}
