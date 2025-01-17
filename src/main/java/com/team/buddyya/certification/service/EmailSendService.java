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
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailSendService {

    private static final int AUTH_CODE_MAX_RANGE = 1_000;

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

    public MimeMessage createMail(String mail, String number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("[버디야] 대학교 인증을 위한 인증번호를 안내 드립니다.");

        String body = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                <h1 style="text-align: center; color: #333;">대학교 이메일 인증 안내</h1>
                <p style="font-size: 16px; color: #555;">안녕하세요, <b>고객님</b></p>
                <p style="font-size: 16px; color: #555;">
                    아래 발급된 이메일 인증번호를 복사하거나 직접 입력하여 인증을 완료해주세요.
                </p>
                <div style="text-align: center; margin: 20px 0;">
                    <span style="display: inline-block; font-size: 24px; color: #0073e6; font-weight: bold; padding: 10px 20px; border: 1px dashed #0073e6; border-radius: 5px;">
                        """ + number + """
                    </span>
                </div>
                <p style="font-size: 16px; color: #555; text-align: center;">감사합니다.</p>
            </div>
            """;

        message.setText(body, "UTF-8", "html");

        return message;
    }

    private String generateRandomNumber() {
        Random rand = new Random();
        return String.format("%04d", rand.nextInt(AUTH_CODE_MAX_RANGE));
    }
}