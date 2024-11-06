package com.team.buddyya.certification.service;

import com.team.buddyya.certification.dto.request.EmailCertificationRequest;
import com.team.buddyya.certification.dto.request.EmailCodeRequest;
import com.team.buddyya.certification.dto.response.EmailCertificationResponse;
import com.univcert.api.UnivCert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
@Transactional
public class CertificationService {

    private String API_KEY = "c86153bb-9519-45ea-a185-3f5a8c50b0eb";

    public EmailCertificationResponse certificateEmail(EmailCertificationRequest emailRequest) throws IOException {
        Map<String, Object> univCertResponse = UnivCert.certify(API_KEY, emailRequest.email(), emailRequest.univName(), true);
        return new EmailCertificationResponse((Boolean) univCertResponse.get("success"));
    }

    public EmailCertificationResponse certificateEmailCode(EmailCodeRequest codeRequest) throws IOException {
        Map<String, Object> univCertResponse = UnivCert.certifyCode(API_KEY, codeRequest.email(), codeRequest.univName(), codeRequest.code());
        return new EmailCertificationResponse((Boolean) univCertResponse.get("success"));
    }
}
