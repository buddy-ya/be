package com.team.buddyya.certification.service;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.response.EmailCertificationResponse;
import com.team.buddyya.certification.exception.EmailAuthException;
import com.team.buddyya.certification.exception.EmailAuthExceptionType;
import com.univcert.api.UnivCert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
@Transactional
public class CertificationService {

    private String API_KEY = "c86153bb-9519-45ea-a185-3f5a8c50b0eb";

    public EmailCertificationResponse certificateEmail(EmailCertificationRequest emailRequest) {
        try {
            Map<String, Object> univCertResponse = UnivCert.certify(API_KEY, emailRequest.email(), emailRequest.univName(), true);
            boolean isSuccess = (Boolean) univCertResponse.get("success");
            return new EmailCertificationResponse(isSuccess);
        } catch (IOException e) {
            throw new EmailAuthException(EmailAuthExceptionType.EMAIL_AUTH_FAILED);
        }
    }

    public EmailCertificationResponse certificateEmailCode(CustomUserDetails userDetails, EmailCodeRequest codeRequest) throws IOException {
        Map<String, Object> univCertResponse = UnivCert.certifyCode(API_KEY, codeRequest.email(), codeRequest.univName(), codeRequest.code());
        return new EmailCertificationResponse((Boolean) univCertResponse.get("success"));
    }
}
