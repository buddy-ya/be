package com.team.buddyya.common;

import com.team.buddyya.feed.domain.Category;
import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.repository.CategoryRepository;
import com.team.buddyya.feed.repository.CommentRepository;
import com.team.buddyya.feed.repository.FeedRepository;
import com.team.buddyya.student.controller.UserController;
import com.team.buddyya.student.domain.Interest;
import com.team.buddyya.student.domain.Language;
import com.team.buddyya.student.domain.Major;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.response.UserResponse;
import com.team.buddyya.student.repository.InterestRepository;
import com.team.buddyya.student.repository.LanguageRepository;
import com.team.buddyya.student.repository.MajorRepository;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final MajorRepository majorRepository;
    private final LanguageRepository languageRepository;
    private final InterestRepository interestRepository;
    private final UniversityRepository universityRepository;
    private final UserController userController;
    private final CategoryRepository categoryRepository;
    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;
    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) throws Exception {
        loadMajors();
        loadLanguages();
        loadInterests();
        loadUniversities();
        createMockStudents();
        loadCategories();
        createMockFeedsAndComments();
    }

    private void loadMajors() {
        List<String> majors = List.of(
                "humanities", "social_sciences", "business", "education", "natural_sciences",
                "it", "engineering", "arts_sports", "nursing", "pharmacy", "veterinary", "medicine"
        );
        majors.forEach(majorName -> {
            if (!majorRepository.findByMajorName(majorName).isPresent()) {
                majorRepository.save(new Major(majorName));
            }
        });
    }

    private void loadLanguages() {
        List<String> languages = List.of(
                "ko", "en", "zh", "ja", "yue", "vi", "th", "id",
                "ms", "tl", "km", "fr", "de", "es", "it", "ru", "hi", "bn",
                "ur", "fa", "ar", "he", "nl", "pl", "tr", "uk", "cs", "sv",
                "hu", "el", "da", "fi", "no", "ro", "sk", "hr", "sr", "sl",
                "bg", "lt", "lv", "et", "is", "af", "sq", "am", "hy", "az",
                "eu", "bs", "ca", "gl", "ka", "gu", "ht", "kn", "kk", "ku",
                "mk", "ml", "mr", "mn", "ne", "ps", "pt", "pa", "so", "sw",
                "ta", "te", "xh", "zu"
        );
        languages.forEach(languageName -> {
            if (!languageRepository.findByLanguageName(languageName).isPresent()) {
                languageRepository.save(new Language(languageName));
            }
        });
    }

    private void loadInterests() {
        List<String> interests = List.of(
                "kpop", "performance", "reading", "movie", "oneday", "experience", "cafe",
                "game", "basketball", "hiking", "running", "bowling", "sports",
                "baseball", "soccer", "tennis", "fitness", "singing", "dance",
                "crafts", "instrument", "photo", "acting", "restaurant", "beer",
                "baking", "cooking", "food_experience"
        );
        interests.forEach(interestName -> {
            if (!interestRepository.findByInterestName(interestName).isPresent()) {
                interestRepository.save(new Interest(interestName));
            }
        });
    }

    private void loadUniversities() {
        List<String> universities = List.of("sju");
        universities.forEach(universityName -> {
            if (!universityRepository.findByUniversityName(universityName).isPresent()) {
                universityRepository.save(new University(universityName, 0L));
            }
        });
    }

    private void createMockStudents() {
        List<OnBoardingRequest> mockRequests = List.of(
                new OnBoardingRequest(
                        "john", "ko", true, true,
                        "01012345678", "male", "sju",
                        List.of("engineering", "it"),
                        List.of("ko", "en"), List.of("performance", "reading")
                ),
                new OnBoardingRequest(
                        "Alice", "us", false, false,
                        "01087654321", "female", "sju",
                        List.of("humanities", "social_sciences"),
                        List.of("en", "ko"), List.of("kpop", "movie")
                )
        );
        mockRequests.forEach(request -> {
            ResponseEntity<UserResponse> response = userController.onboard(request);
        });
    }

    private void loadCategories() {
        List<String> categories = List.of(
                "free", "popular", "recruitment", "info"
        );
        categories.forEach(categoryName -> {
            if (!categoryRepository.existsByName(categoryName)) {
                categoryRepository.save(new Category(categoryName));
            }
        });
    }

    private void createMockFeedsAndComments() {
        Student student = studentRepository.findAll().get(0);
        Category category = categoryRepository.findAll().get(0);
        List<Feed> mockFeeds = List.of(
                Feed.builder()
                        .title("학냥이 너무 귀여운 것 같아")
                        .content("잘 찍었지? 학냥이가 너무너무 좋아. 잔디밭에서 뒹구는 것 봐 ㅎㅎㅎ")
                        .student(student)
                        .category(category)
                        .university(student.getUniversity())
                        .build(),
                Feed.builder()
                        .title("글글글 글글 글글 글")
                        .content("맥주랑 크키카칵\n카스다카스다 어쩌고 ...")
                        .student(student)
                        .category(category)
                        .university(student.getUniversity())
                        .build(),
                Feed.builder()
                        .title("영국 맨체스터 대학교 교환학생 후기")
                        .content("안녕하세요! 저는 이번에 맨체스터 대학교에서 한 학기를 보내고 왔습니다. 정말 값진 경험이었어요...")
                        .student(student)
                        .category(category)
                        .university(student.getUniversity())
                        .build()
        );
        mockFeeds.forEach(feed -> feedRepository.save(feed));
        List<Comment> mockComments = List.of(
                Comment.builder()
                        .content("와 대박 ㅋㅋ 나도 학교 고양이 좋아하는데!")
                        .feed(mockFeeds.get(0))
                        .student(student)
                        .build(),
                Comment.builder()
                        .content("우리 학교 근처에도 있어요")
                        .feed(mockFeeds.get(0))
                        .student(student)
                        .build(),
                Comment.builder()
                        .content("귀엽다 ㅎㅎ")
                        .feed(mockFeeds.get(0))
                        .student(student)
                        .build()
        );
        mockComments.forEach(comment -> commentRepository.save(comment));
        IntStream.range(0, 15).forEach(i -> {
            Feed feed = Feed.builder()
                    .title("Sample Feed Title " + i)
                    .content("Sample Feed Content " + i)
                    .student(student)
                    .category(category)
                    .university(student.getUniversity())
                    .build();
            feedRepository.save(feed);

            IntStream.range(0, 15).forEach(j -> {
                Comment comment = Comment.builder()
                        .content("Sample Comment " + j + " for Feed " + i)
                        .feed(feed)
                        .student(student)
                        .build();
                commentRepository.save(comment);
            });
        });
    }

}
