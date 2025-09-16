package com.team.buddyya.feed.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.service.EmailSendService;
import com.team.buddyya.certification.service.MessageSendService;
import com.team.buddyya.common.config.S3Config;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.feed.domain.Category;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.repository.CategoryRepository;
import com.team.buddyya.feed.repository.FeedLikeRepository;
import com.team.buddyya.feed.repository.FeedRepository;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.student.domain.Gender;
import com.team.buddyya.student.domain.Role;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class FeedLikeServiceConcurrencyTest {

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private EmailSendService emailSendService;

    @MockBean
    private MessageSendService messageSendService;

    @MockBean
    private S3UploadService s3UploadService;

    @MockBean
    private S3Config s3Config;

    @Autowired
    private FeedLikeService feedLikeService;
    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private FeedLikeRepository feedLikeRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UniversityRepository universityRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Feed savedFeed;
    private List<Student> likerStudents;

    @BeforeEach
    void setUp() {
        feedLikeRepository.deleteAllInBatch();
        feedRepository.deleteAllInBatch();
        studentRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        universityRepository.deleteAllInBatch();

        University testUniversity = universityRepository.save(new University("sejong"));
        Category testCategory = categoryRepository.save(new Category("자유게시판"));

        Student feedOwner = createAndSaveStudent("01000000000", "피드주인", testUniversity);
        savedFeed = feedRepository.save(Feed.builder()
                .student(feedOwner)
                .title("동시성 테스트 피드")
                .content("내용입니다.")
                .university(testUniversity)
                .category(testCategory)
                .isProfileVisible(true)
                .build());

        likerStudents = IntStream.range(1, 101)
                .mapToObj(i -> createAndSaveStudent(
                        String.format("010%08d", i),
                        "user" + i,
                        testUniversity))
                .collect(Collectors.toList());
    }

    @Test
    void 동시에_좋아요를_누르면_갱신손실이_발생한다() throws InterruptedException {
        // given
        int userCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(userCount);

        // when
        for (Student liker : likerStudents) {
            executorService.submit(() -> {
                try {
                    StudentInfo studentInfo = new StudentInfo(liker.getId(), liker.getRole(), true);
                    feedLikeService.toggleLike(studentInfo, savedFeed.getId());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        long actualLikeRows = feedLikeRepository.countByFeed(savedFeed);
        Feed finalFeed = feedRepository.findById(savedFeed.getId()).orElseThrow();
        int finalLikeCount = finalFeed.getLikeCount();

        System.out.println("==============================================");
        System.out.println("실제 생성된 FeedLike 레코드 수: " + actualLikeRows);
        System.out.println("Feed 엔티티에 기록된 최종 likeCount: " + finalLikeCount);
        System.out.println("==============================================");

        assertThat(actualLikeRows).isEqualTo(userCount);
        assertThat(finalLikeCount).isNotEqualTo(userCount);
    }

    @Test
    void 트랜잭션_격리_수준을_SERIALIZABLE로_설정시_동시성_문제가_해결된다() throws InterruptedException {
        // given
        int userCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(userCount);

        // when
        for (Student liker : likerStudents) {
            executorService.submit(() -> {
                try {
                    StudentInfo studentInfo = new StudentInfo(liker.getId(), liker.getRole(), true);
                    feedLikeService.toggleLike(studentInfo, savedFeed.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        long actualLikeRows = feedLikeRepository.countByFeed(savedFeed);
        Feed finalFeed = feedRepository.findById(savedFeed.getId()).orElseThrow();
        int finalLikeCount = finalFeed.getLikeCount();

        System.out.println("==============================================");
        System.out.println("[SERIALIZABLE] 실제 생성된 FeedLike 레코드 수: " + actualLikeRows);
        System.out.println("[SERIALIZABLE] Feed 엔티티에 기록된 최종 likeCount: " + finalLikeCount);
        System.out.println("==============================================");

        assertThat(actualLikeRows).isEqualTo(userCount);
        assertThat(finalLikeCount).isEqualTo(userCount);
    }

    @Test
    void synchronized_키워드_적용시_동시성_문제가_해결된다() throws InterruptedException {
        // given
        int userCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(userCount);

        // when
        for (Student liker : likerStudents) {
            executorService.submit(() -> {
                try {
                    StudentInfo studentInfo = new StudentInfo(liker.getId(), liker.getRole(), true);
                    feedLikeService.toggleLike(studentInfo, savedFeed.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        long actualLikeRows = feedLikeRepository.countByFeed(savedFeed);
        Feed finalFeed = feedRepository.findById(savedFeed.getId()).orElseThrow();
        int finalLikeCount = finalFeed.getLikeCount();

        System.out.println("==============================================");
        System.out.println("[synchronized] 실제 생성된 FeedLike 레코드 수: " + actualLikeRows);
        System.out.println("[synchronized] Feed 엔티티에 기록된 최종 likeCount: " + finalLikeCount);
        System.out.println("==============================================");

        assertThat(actualLikeRows).isEqualTo(userCount);
        assertThat(finalLikeCount).isEqualTo(userCount);
    }

    @Test
    void 낙관적_락_적용시_쓰기_충돌이_발생하면_예외가_발생한다() throws InterruptedException {
        // given
        int userCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(userCount);

        // when
        for (Student liker : likerStudents) {
            executorService.submit(() -> {
                try {
                    StudentInfo studentInfo = new StudentInfo(liker.getId(), liker.getRole(), true);
                    feedLikeService.toggleLike(studentInfo, savedFeed.getId());
                } catch (Exception e) {
                    System.out.println("충돌 감지: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        long actualLikeRows = feedLikeRepository.countByFeed(savedFeed);
        Feed finalFeed = feedRepository.findById(savedFeed.getId()).orElseThrow();
        int finalLikeCount = finalFeed.getLikeCount();

        System.out.println("==============================================");
        System.out.println("[Optimistic Lock] 실제 생성된 FeedLike 레코드 수: " + actualLikeRows);
        System.out.println("[Optimistic Lock] Feed 엔티티에 기록된 최종 likeCount: " + finalLikeCount);
        System.out.println("==============================================");

        assertThat(actualLikeRows).isNotEqualTo(userCount);
        assertThat(finalLikeCount).isNotEqualTo(userCount);
        assertThat(actualLikeRows).isEqualTo(finalLikeCount);
    }

    @Test
    void 비관적_락_적용시_동시성_문제가_해결된다() throws InterruptedException {
        // given
        int userCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(userCount);

        // when
        for (Student liker : likerStudents) {
            executorService.submit(() -> {
                try {
                    StudentInfo studentInfo = new StudentInfo(liker.getId(), liker.getRole(), true);
                    feedLikeService.toggleLike(studentInfo, savedFeed.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        long actualLikeRows = feedLikeRepository.countByFeed(savedFeed);
        Feed finalFeed = feedRepository.findById(savedFeed.getId()).orElseThrow();
        int finalLikeCount = finalFeed.getLikeCount();

        System.out.println("==============================================");
        System.out.println("[Pessimistic Lock] 실제 생성된 FeedLike 레코드 수: " + actualLikeRows);
        System.out.println("[Pessimistic Lock] Feed 엔티티에 기록된 최종 likeCount: " + finalLikeCount);
        System.out.println("==============================================");

        assertThat(actualLikeRows).isEqualTo(userCount);
        assertThat(finalLikeCount).isEqualTo(userCount);
    }

    private Student createAndSaveStudent(String phoneNumber, String name, University university) {
        Student student = Student.builder()
                .phoneNumber(phoneNumber)
                .name(name)
                .university(university)
                .country("South Korea")
                .isKorean(true)
                .role(Role.STUDENT)
                .gender(Gender.MALE)
                .characterProfileImage("default.png")
                .build();
        return studentRepository.save(student);
    }
}
