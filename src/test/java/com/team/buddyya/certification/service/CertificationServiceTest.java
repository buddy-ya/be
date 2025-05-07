package com.team.buddyya.certification.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.certification.dto.request.SendStudentIdCardRequest;
import com.team.buddyya.certification.dto.response.CertificationResponse;
import com.team.buddyya.certification.exception.CertificateException;
import com.team.buddyya.certification.exception.CertificateExceptionType;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CertificationServiceTest {

    @Mock
    private FindStudentService findStudentService;

    @Mock
    private StudentIdCardRepository studentIdCardRepository;

    @Mock
    private S3UploadService s3UploadService;

    @InjectMocks
    private CertificationService certificationService;

    private StudentInfo info;
    private Student student;
    private MultipartFile file;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        info = mock(StudentInfo.class);
        when(info.id()).thenReturn(42L);
        student = mock(Student.class);
        when(student.getIsCertificated()).thenReturn(false);
        when(findStudentService.findByStudentId(42L)).thenReturn(student);
        file = mock(MultipartFile.class);
    }

    @Test
    @DisplayName("새로운 학생증 업로드 시, 저장 후 성공 응답을 반환해야 한다")
    void uploadStudentIdCard_newCard_savesAndReturnsTrue() {
        //given
        when(s3UploadService.uploadFile(anyString(), eq(file))).thenReturn("uploaded-url");
        when(studentIdCardRepository.findByStudent(student)).thenReturn(Optional.empty());

        //when
        CertificationResponse response = certificationService.uploadStudentIdCard(info, new SendStudentIdCardRequest(file));

        //then
        assertThat(response.success()).isTrue();
        verify(studentIdCardRepository).save(argThat(card ->
                card.getImageUrl().equals("uploaded-url") &&
                        card.getStudent() == student
        ));
    }

    @Test
    @DisplayName("기존 학생증이 있을 때 업로드 시, 기존 파일 삭제 후 URL만 업데이트 한다")
    void uploadStudentIdCard_existingCard_updatesImageAndDeletesOldFile() {
        //given
        when(s3UploadService.uploadFile(anyString(), eq(file))).thenReturn("new-url");
        StudentIdCard existing = StudentIdCard.builder()
                .imageUrl("old-url")
                .student(student)
                .build();
        when(studentIdCardRepository.findByStudent(student)).thenReturn(Optional.of(existing));

        //when
        CertificationResponse response = certificationService.uploadStudentIdCard(info, new SendStudentIdCardRequest(file));

        //then
        assertThat(response.success()).isTrue();
        verify(s3UploadService).deleteFile(anyString(), eq("old-url"));
        assertThat(existing.getImageUrl()).isEqualTo("new-url");
    }

    @Test
    @DisplayName("이미 인증된 학생이 학생증 업로드 요청 시 ALREADY_CERTIFICATED 예외를 던져야 한다")
    void uploadStudentIdCard_alreadyCertificated_throwsException() {
        //given
        when(student.getIsCertificated()).thenReturn(true);

        //when
        CertificateException ex = assertThrows(CertificateException.class, () ->
                certificationService.uploadStudentIdCard(info, new SendStudentIdCardRequest(file))
        );
        assertThat(ex.exceptionType())
                .isEqualTo(CertificateExceptionType.ALREADY_CERTIFICATED);

        //then
        verify(s3UploadService, never()).uploadFile(anyString(), any());
        verify(studentIdCardRepository, never()).save(any());
    }
}
