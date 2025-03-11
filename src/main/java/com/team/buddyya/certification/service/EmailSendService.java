package com.team.buddyya.certification.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.exception.CertificateException;
import com.team.buddyya.certification.exception.CertificateExceptionType;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import com.team.buddyya.student.service.StudentService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailSendService {

    private static final int AUTH_CODE_MAX_RANGE = 10_000;

    private final JavaMailSender javaMailSender;
    private final StudentService studentService;
    private final FindStudentService findStudentService;

    @Value("${GOOGLE_SMTP_EMAIL}")
    private String senderEmail;

    public String sendEmail(StudentInfo studentInfo, EmailCertificationRequest emailRequest) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        validateStatusAndEmail(emailRequest, student);
        String generatedCode = generateRandomNumber();
        try {
            String emailContent = getEmailTemplate(student.getIsKorean(), generatedCode);
            MimeMessage message = createMail(emailRequest.email(), emailContent);
            javaMailSender.send(message);
            return generatedCode;
        } catch (IOException | MessagingException | MailException e) {
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

    private String getEmailTemplate(boolean isKorean, String code) throws IOException {
        String templatePath = isKorean ? "templates/email_korean.html" : "templates/email_english.html";
        ClassPathResource resource = new ClassPathResource(templatePath);
        String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        return content.replace("{{code}}", code);
    }

    public MimeMessage createMail(String mail, String emailContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("[Buddyya] University Email Verification Code");
        message.setText(emailContent, "UTF-8", "html");
        return message;
    }

    private String generateRandomNumber() {
        Random rand = new Random();
        return String.format("%04d", rand.nextInt(AUTH_CODE_MAX_RANGE));
    }
}
