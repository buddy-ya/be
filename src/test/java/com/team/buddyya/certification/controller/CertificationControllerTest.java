package com.team.buddyya.certification.controller;

import com.team.buddyya.certification.dto.request.SendStudentIdCardRequest;
import com.team.buddyya.certification.dto.response.StudentIdCardResponse;
import com.team.buddyya.certification.service.CertificationService;
import com.team.buddyya.certification.service.EmailSendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CertificationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
@ActiveProfiles("test")
class CertificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificationService certificationService;

    @MockBean
    private EmailSendService emailSendService;

    private MockMultipartFile multipartFile;
    private StudentIdCardResponse idCardResponse;

    @BeforeEach
    void setUp() {
        multipartFile = new MockMultipartFile(
                "image",
                "idcard.png",
                "image/png",
                "dummy-content".getBytes()
        );

        doNothing().when(certificationService)
                .uploadStudentIdCard(any(), any(SendStudentIdCardRequest.class));

        idCardResponse = new StudentIdCardResponse("http://example.com/id.png", "none");
        when(certificationService.getStudentIdCard(any()))
                .thenReturn(idCardResponse);
    }

    @Test
    @DisplayName("학생증 업로드 요청 시 204 No Content 반환")
    void sendStudentIdCard_returnsNoContent() throws Exception {
        mockMvc.perform(multipart("/certification/student-id-card")
                        .file(multipartFile))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("학생증 조회 요청 시 200 OK 및 URL·거절 사유 반환")
    void getStudentIdCard_returnsUrlAndReason() throws Exception {
        mockMvc.perform(get("/certification/student-id-card"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentIdCardUrl").value("http://example.com/id.png"))
                .andExpect(jsonPath("$.rejectionReason").value("none"));
    }
}
