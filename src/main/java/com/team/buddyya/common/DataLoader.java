package com.team.buddyya.common;

import com.team.buddyya.student.controller.OnBoardingController;
import com.team.buddyya.student.domain.Interest;
import com.team.buddyya.student.domain.Language;
import com.team.buddyya.student.domain.University;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.response.OnBoardingResponse;
import com.team.buddyya.student.repository.InterestRepository;
import com.team.buddyya.student.repository.LanguageRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final LanguageRepository languageRepository;
    private final InterestRepository interestRepository;
    private final UniversityRepository universityRepository;
    private final OnBoardingController onBoardingController;

    @Override
    public void run(String... args) throws Exception {
        loadLanguages();
        loadInterests();
        loadUniversities();
        createMockStudents();
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
                        "홍길동", "Computer Science", "ko", true,
                        true, "01012345678", "male",
                         "sju", List.of("ko", "en"), List.of("performance", "reading")
                ),
                new OnBoardingRequest(
                        "Alice", "Computer Science", "Canada", false,
                        true, "01087654321", "female",
                        "sju", List.of("en", "ko"), List.of("kpop", "movie")
                )
        );
        mockRequests.forEach(request -> {
            ResponseEntity<OnBoardingResponse> response = onBoardingController.onboard(request);
        });
    }
}
