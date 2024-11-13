package com.team.buddyya.common;

import com.team.buddyya.student.domain.Interest;
import com.team.buddyya.student.domain.Language;
import com.team.buddyya.student.domain.University;
import com.team.buddyya.student.repository.InterestRepository;
import com.team.buddyya.student.repository.LanguageRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final LanguageRepository languageRepository;
    private final InterestRepository interestRepository;
    private final UniversityRepository universityRepository;

    @Override
    public void run(String... args) throws Exception {
        loadLanguages();
        loadInterests();
        loadUniversities();
    }

    private void loadLanguages() {
        List<String> languages = List.of(
                "Korean", "English", "Chinese", "Japanese", "French", "Spanish", "Burmese",
                "Portuguese", "German", "Nepali", "Mongolian", "Swahili", "Arabic",
                "Uzbek", "Indonesian", "Khmer", "Persian", "Hebrew", "Hindi",
                "Greek", "Netherlandic", "Latin", "Russian", "Rumanian", "Albanian",
                "Ukrainian", "Italian", "Georgian", "Czech", "Slovak", "Turkish",
                "Polish", "Finnish", "Hungarian", "Thai", "Swedish", "Danish",
                "Vietnamese", "Norwegian", "Catalan", "Croatian", "Malay",
                "Kazakh", "Bulgarian"
        );
        languages.forEach(languageName -> {
            if (!languageRepository.findByLanguageName(languageName).isPresent()) {
                languageRepository.save(new Language(languageName));
            }
        });
    }

    private void loadInterests() {
        List<String> interests = List.of(
                "K-POP", "공연 & 전시회 관람", "독서", "영화", "원데이클래스", "체험카페", "카페 투어",
                "게임", "농구", "등산", "러닝", "볼링", "스포츠 경기 관람", "야구", "축구", "테니스",
                "헬스", "노래", "춤", "공예", "악기 연주", "사진 촬영", "연기", "맛집 투어", "맥주",
                "베이킹", "요리", "이색 음식 체험"
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
}
