package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.Language;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.StudentLanguage;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.LanguageRepository;
import com.team.buddyya.student.repository.StudentLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentLanguageService {

    private final StudentLanguageRepository studentLanguageRepository;
    private final LanguageRepository languageRepository;

    public void createStudentLanguages(List<String> languages, Student student) {
        languages.forEach(languageName -> {
            Language language = languageRepository.findByLanguageName(languageName)
                    .orElseThrow(() -> new StudentException(StudentExceptionType.LANGUAGE_NOT_FOUND));
            StudentLanguage studentLanguage = StudentLanguage.builder()
                    .student(student)
                    .language(language)
                    .build();

            studentLanguage.setStudent(student);

            studentLanguageRepository.save(studentLanguage);
        });
    }

    public void updateStudentLanguages(List<String> languages, Student student) {
        studentLanguageRepository.deleteByStudent(student);
        createStudentLanguages((languages), student);
    }
}
