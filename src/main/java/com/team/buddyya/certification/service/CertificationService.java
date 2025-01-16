package com.team.buddyya.certification.service;

import static com.team.buddyya.common.domain.S3DirectoryName.STUDENT_ID_CARD;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.domain.StudentEmail;
import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.request.SendStudentIdCardRequest;
import com.team.buddyya.certification.dto.response.CertificationResponse;
import com.team.buddyya.certification.dto.response.CertificationStatusResponse;
import com.team.buddyya.certification.dto.response.StudentIdCardResponse;
import com.team.buddyya.certification.exception.CertificateException;
import com.team.buddyya.certification.exception.CertificateExceptionType;
import com.team.buddyya.certification.repository.StudentEmailRepository;
import com.team.buddyya.certification.repository.StudentIdCardRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import com.team.buddyya.student.service.StudentService;

import java.util.Optional;
import java.util.Random;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificationService {

    private final FindStudentService findStudentService;
    private final StudentIdCardRepository studentIdCardRepository;
    private final StudentEmailRepository studentEmailRepository;
    private final S3UploadService s3UploadService;
    private final StudentService studentService;

    private final JavaMailSender javaMailSender;
    private static final int AUTH_CODE_MAX_RANGE = 1_000;

    @Value("${GOOGLE_SMTP_EMAIL}")
    private String senderEmail;

    public String generateRandomNumber() {
        Random rand = new Random();
        return String.format("%04d", rand.nextInt(AUTH_CODE_MAX_RANGE));
    }

    public MimeMessage createMail(String mail, String number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + number + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    public String sendEmail(StudentInfo studentInfo, EmailCertificationRequest emailRequest) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        validateStatusAndEmail(emailRequest, student);
        String generatedCode = generateRandomNumber();
        try {
            MimeMessage message = createMail(emailRequest.email(), generatedCode);
            javaMailSender.send(message);
            return generatedCode;
        } catch (MessagingException | MailException e) {
            throw new CertificateException(CertificateExceptionType.EMAIL_SEND_FAILED);
        }
    }

    private void validateStatusAndEmail(EmailCertificationRequest emailRequest, Student student) {
        if (student.getIsCertificated()) {
            throw new CertificateException(CertificateExceptionType.ALREADY_CERTIFICATED);
        }
        if (studentService.isDuplicateStudentEmail(emailRequest.email())) {
            throw new CertificateException(CertificateExceptionType.DUPLICATED_EMAIL_ADDRESS);
        }
    }

    public CertificationResponse saveCode(String email, String generatedCode) {
        StudentEmail studentEmail = studentEmailRepository.findByEmail(email)
                .orElseGet(() -> new StudentEmail(email, generatedCode));
        if (studentEmail.getId() != null) {
            studentEmail.updateAuthenticationCode(generatedCode);
        }
        studentEmailRepository.save(studentEmail);
        return CertificationResponse.from(true);
    }

    public CertificationResponse certificateEmailCode(StudentInfo studentInfo, EmailCodeRequest codeRequest) {
        StudentEmail studentEmail = studentEmailRepository.findByEmail(codeRequest.email())
                .orElseThrow(() -> new CertificateException(CertificateExceptionType.STUDENT_EMAIL_NOT_FOUND));
        if (!codeRequest.email().equals(studentEmail.getAuthenticationCode())) {
            throw new CertificateException(CertificateExceptionType.CODE_MISMATCH);
        }
        Student student = findStudentService.findByStudentId(studentInfo.id());
        updateCertification(codeRequest, student);
        return CertificationResponse.from(true);
    }

    private void updateCertification(EmailCodeRequest codeRequest, Student student) {
        student.updateIsCertificated(true);
        student.updateEmail(codeRequest.email());
    }

    public CertificationResponse uploadStudentIdCard(StudentInfo studentInfo,
                                                     SendStudentIdCardRequest sendStudentIdCardRequest) {
        MultipartFile file = sendStudentIdCardRequest.image();
        Student student = findStudentService.findByStudentId(studentInfo.id());
        if (student.getIsCertificated()) {
            throw new CertificateException(CertificateExceptionType.ALREADY_CERTIFICATED);
        }
        String imageUrl = s3UploadService.uploadFile(STUDENT_ID_CARD.getDirectoryName(), file);
        Optional<StudentIdCard> existStudentIdCard = studentIdCardRepository.findByStudent(student);
        if (existStudentIdCard.isPresent()) {
            return updateExistingStudentIdCard(imageUrl, existStudentIdCard.get());
        }
        StudentIdCard studentIdCard = StudentIdCard.builder()
                .imageUrl(imageUrl)
                .student(student)
                .build();
        studentIdCardRepository.save(studentIdCard);
        return CertificationResponse.from(true);
    }

    private CertificationResponse updateExistingStudentIdCard(String imageUrl, StudentIdCard existStudentIdCard) {
        s3UploadService.deleteFile(STUDENT_ID_CARD.getDirectoryName(), existStudentIdCard.getImageUrl());
        existStudentIdCard.updateImageUrl(imageUrl);
        return CertificationResponse.from(true);
    }

    @Transactional(readOnly = true)
    public CertificationStatusResponse isCertificated(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        boolean isStudentIdCardRequested = studentIdCardRepository.findByStudent(student)
                .isPresent();
        return CertificationStatusResponse.from(student, isStudentIdCardRequested);
    }

    @Transactional(readOnly = true)
    public StudentIdCardResponse getStudentIdCard(StudentInfo studentInfo) {
        StudentIdCard studentIdCard = studentIdCardRepository.findByStudent_Id(studentInfo.id())
                .orElseThrow(() -> new CertificateException(CertificateExceptionType.STUDENT_ID_CARD_NOT_FOUND));
        return StudentIdCardResponse.from(studentIdCard.getImageUrl());
    }

    public void refreshStudentCertification(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        student.updateIsCertificated(false);
        student.updateEmail(null);
        studentIdCardRepository.deleteByStudent(student);
    }
}
