package com.team.buddyya.certification.repository;

import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.student.domain.Gender;
import com.team.buddyya.student.domain.Role;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StudentIdCardRepositoryTest {

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentIdCardRepository studentIdCardRepository;

    private University defaultUni;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        // 이제 모두 레포지토리 save() 로 저장
        defaultUni = universityRepository.save(
                University.builder()
                        .universityName("테스트대학")
                        .build()
        );

        student1 = studentRepository.save(
                Student.builder()
                        .name("학생A")
                        .phoneNumber("01000000001")
                        .country("KR")
                        .isKorean(true)
                        .role(Role.STUDENT)
                        .university(defaultUni)
                        .gender(Gender.MALE)
                        .characterProfileImage("char1.png")
                        .build()
        );

        student2 = studentRepository.save(
                Student.builder()
                        .name("학생B")
                        .phoneNumber("01000000002")
                        .country("KR")
                        .isKorean(true)
                        .role(Role.STUDENT)
                        .university(defaultUni)
                        .gender(Gender.FEMALE)
                        .characterProfileImage("char2.png")
                        .build()
        );
    }

    @Test
    @DisplayName("findByStudent() — 해당 학생의 카드 Optional 반환")
    void findByStudent_returnsOptional() {
        // given
        StudentIdCard card = studentIdCardRepository.save(
                StudentIdCard.builder()
                        .imageUrl("url1")
                        .student(student1)
                        .build()
        );

        // when
        Optional<StudentIdCard> found = studentIdCardRepository.findByStudent(student1);

        // then
        assertThat(found).isPresent()
                .get()
                .extracting(StudentIdCard::getImageUrl)
                .isEqualTo("url1");
    }

    @Test
    @DisplayName("findAllByOrderByCreatedDateAsc() — 생성일 기준 오름차순 리스트 반환")
    void findAllByOrderByCreatedDateAsc_returnsSortedList() {
        // given
        studentIdCardRepository.save(
                StudentIdCard.builder().imageUrl("u1").student(student1).build());
        studentIdCardRepository.save(
                StudentIdCard.builder().imageUrl("u2").student(student2).build());

        // when
        List<StudentIdCard> list = studentIdCardRepository.findAllByOrderByCreatedDateAsc();

        // then
        assertThat(list).hasSize(2)
                .extracting(StudentIdCard::getImageUrl)
                .containsExactly("u1", "u2");
    }
    
    @Test
    @DisplayName("findUploadStudentIdCards() — rejectionReason이 NULL인 카드만 반환")
    void findUploadStudentIdCards_filtersNullRejectionReason() {
        // given
        studentIdCardRepository.save(
                StudentIdCard.builder().imageUrl("good").student(student1).build());
        StudentIdCard badCard = studentIdCardRepository.save(
                StudentIdCard.builder().imageUrl("bad").student(student2).build());
        badCard.updateRejectionReason("반려");

        // when
        List<StudentIdCard> uploads = studentIdCardRepository.findUploadStudentIdCards();

        // then
        assertThat(uploads).hasSize(1)
                .first()
                .extracting(StudentIdCard::getImageUrl)
                .isEqualTo("good");
    }

    @Test
    @DisplayName("deleteByStudent() — 학생별 카드 삭제")
    void deleteByStudent_removesCard() {
        // given
        StudentIdCard card = studentIdCardRepository.save(
                StudentIdCard.builder().imageUrl("toDelete").student(student1).build()
        );

        // when
        studentIdCardRepository.deleteByStudent(student1);
        Optional<StudentIdCard> after = studentIdCardRepository.findByStudent(student1);

        // then
        assertThat(after).isEmpty();
    }
}
