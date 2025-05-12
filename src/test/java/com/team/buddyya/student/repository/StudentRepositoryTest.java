package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.*;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @Test
    void Student를_정상적으로_저장한다() {
        // given
        University university = universityRepository.save(
                University.builder()
                        .universityName("sju")
                        .build()
        );
        CharacterProfileImage randomImage = CharacterProfileImage.getRandomProfileImage();
        Student student = Student.builder()
                .name("test")
                .phoneNumber("01012345678")
                .country("kr")
                .isKorean(true)
                .role(Role.STUDENT)
                .university(university)
                .gender(Gender.MALE)
                .characterProfileImage(randomImage.getUrl())
                .build();

        // when
        Student savedStudent = studentRepository.save(student);

        // then
        assertThat(savedStudent.getId()).isNotNull();
    }
}
