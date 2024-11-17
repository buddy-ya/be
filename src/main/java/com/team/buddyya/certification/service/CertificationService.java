package com.team.buddyya.certification.service;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.response.EmailCertificationResponse;
import com.team.buddyya.certification.exception.EmailAuthException;
import com.team.buddyya.certification.exception.EmailAuthExceptionType;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.OnBoardingException;
import com.team.buddyya.student.exception.OnBoardingExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import com.univcert.api.UnivCert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificationService {

    private String API_KEY = "c86153bb-9519-45ea-a185-3f5a8c50b0eb";

    private final StudentRepository studentRepository;

    public EmailCertificationResponse certificateEmail(CustomUserDetails userDetails, EmailCertificationRequest emailRequest) {
        Student student = studentRepository.findById(userDetails.getStudentInfo().id())
                .orElseThrow(() -> new OnBoardingException(OnBoardingExceptionType.STUDENT_NOT_FOUND));
        if (student.getIsCertificated()) {
            throw new EmailAuthException(EmailAuthExceptionType.ALREADY_CERTIFICATED);
        }
        try {
            Map<String, Object> univCertResponse = UnivCert.certify(API_KEY, emailRequest.email(), emailRequest.univName(), true);
            boolean isSuccess = (Boolean) univCertResponse.get("success");
            return new EmailCertificationResponse(isSuccess);
        } catch (IOException e) {
            throw new EmailAuthException(EmailAuthExceptionType.EMAIL_AUTH_FAILED);
        }
    }

    public EmailCertificationResponse certificateEmailCode(CustomUserDetails userDetails, EmailCodeRequest codeRequest) {
        try {
            Map<String, Object> univCertResponse = UnivCert.certifyCode(API_KEY, codeRequest.email(), codeRequest.univName(), codeRequest.code());
            Boolean isSuccess = (Boolean) univCertResponse.get("success");
            if(isSuccess){
                Student student = studentRepository.findById(userDetails.getStudentInfo().id())
                        .orElseThrow(() -> new OnBoardingException(OnBoardingExceptionType.STUDENT_NOT_FOUND));
                student.checkIsCertificated(true);
                studentRepository.save(student);
            }
            UnivCert.clear(API_KEY);
            return new EmailCertificationResponse(isSuccess);
        } catch (IOException e) {
            throw new EmailAuthException(EmailAuthExceptionType.EMAIL_AUTH_FAILED);
        }
    }
}
