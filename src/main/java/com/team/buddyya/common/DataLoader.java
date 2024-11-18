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
                "ko", "cn", "hk", "jp", "mo", "mn", "tw", "bn", "kh", "id", "la", "my",
                "mm", "ph", "sg", "th", "tl", "vn", "bd", "bt", "in", "mv", "np", "pk",
                "lk", "kz", "kg", "tj", "tm", "uz", "ca", "mx", "us", "cr", "cu", "do",
                "sv", "gt", "pa", "ar", "bo", "br", "cl", "co", "ec", "py", "pe", "uy",
                "ve", "at", "be", "fr", "de", "gr", "ie", "it", "lu", "nl", "pt", "es",
                "ch", "gb", "dk", "ee", "fi", "is", "lv", "lt", "no", "se", "by", "bg",
                "hr", "cz", "hu", "pl", "ro", "ru", "sk", "si", "ua", "au", "fj", "nz",
                "eg", "ir", "iq", "il", "jo", "kw", "lb", "ma", "om", "qa", "sa", "tr", "ae"
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
        List<String> universities = List.of(
                "Sejong University", "Seoul National University", "Korea University",
                "Yonsei University", "Hanyang University", "Sungkyunkwan University",
                "KAIST", "POSTECH", "GIST", "Konkuk University"
        );
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
                         "Sejong University", List.of("ko", "en"), List.of("performance", "reading")
                ),
                new OnBoardingRequest(
                        "Alice", "Computer Science", "Canada", false,
                        true, "01087654321", "female",
                        "Sejong University", List.of("en", "ko"), List.of("kpop", "movie")
                )
        );
        mockRequests.forEach(request -> {
            ResponseEntity<OnBoardingResponse> response = onBoardingController.onboard(request);
        });
    }
}
