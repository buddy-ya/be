package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.Language;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.StudentLanguage;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.exception.OnBoardingException;
import com.team.buddyya.student.exception.OnBoardingExceptionType;
import com.team.buddyya.student.repository.LanguageRepository;
import com.team.buddyya.student.repository.StudentLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentLanguageService {

    private final StudentLanguageRepository studentLanguageRepository;
    private final LanguageRepository languageRepository;
    public void createStudentLanguages(OnBoardingRequest request, Student student) {
        request.languages().forEach(languageName -> {
            Language language = languageRepository.findByLanguageName(languageName)
                    .orElseThrow(() -> new OnBoardingException(OnBoardingExceptionType.LANGUAGE_NOT_FOUND));
            studentLanguageRepository.save(StudentLanguage.builder()
                    .student(student)
                    .language(language)
                    .build());
        });
    }
}
