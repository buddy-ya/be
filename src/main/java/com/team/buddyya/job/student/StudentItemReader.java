package com.team.buddyya.job.student;

import com.team.buddyya.student.domain.University;
import com.team.buddyya.student.repository.UniversityRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

@Slf4j
public class StudentItemReader implements ItemReader<StudentJobDTO> {

    private final UniversityRepository universityRepository;
    private final int totalCount;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Random random = new Random();
    private List<University> universities;

    public StudentItemReader(UniversityRepository universityRepository, int totalCount) {
        this.universityRepository = universityRepository;
        this.totalCount = totalCount;
    }

    @Override
    public StudentJobDTO read() {
        if (this.universities == null) {
            this.universities = universityRepository.findAll();
        }
        int currentCount = counter.getAndIncrement();
        if (currentCount >= totalCount) {
            return null;
        }
        int index = currentCount + 1;
        University randomUniversity = universities.get(random.nextInt(universities.size()));
        return StudentJobDTO.builder()
                .phoneNumber("010" + String.format("%08d", index))
                .name("student" + index)
                .country("ko")
                .isCertificated(false)
                .isKorean(true)
                .isDeleted(false)
                .universityId(randomUniversity.getId())
                .role("STUDENT")
                .gender(index % 2 == 0 ? "MALE" : "FEMALE")
                .characterProfileImage("default_image_url_" + (index % 8))
                .isBanned(false)
                .build();
    }
}
