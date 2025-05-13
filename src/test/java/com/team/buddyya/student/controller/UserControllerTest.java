package com.team.buddyya.student.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.feed.service.FeedService;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.response.UserResponse;
import com.team.buddyya.student.service.InvitationService;
import com.team.buddyya.student.service.OnBoardingService;
import com.team.buddyya.student.service.StudentService;
import com.team.buddyya.student.service.UniversityService;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @MockBean
    private OnBoardingService onBoardingService;

    @MockBean
    private FeedService feedService;

    @MockBean
    private UniversityService universityService;

    @MockBean
    private InvitationService invitationService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @WithMockUser
    @Test
    void 온보딩_요청이_정상적으로_처리된다() throws Exception {
        // given
        OnBoardingRequest request = new OnBoardingRequest(
                "John",
                "us",
                false,
                true,
                "01012345678",
                "male",
                "sju",
                List.of("humanities", "social_sciences"),
                List.of("ko", "en"),
                List.of("kpop", "movie")
        );

        UserResponse response = new UserResponse(
                1L, "STUDENT", "John", "us", "sju", "male", "https://img.com",
                false, false, false, true,
                List.of("humanities", "social_sciences"), List.of("ko", "en"), List.of("kpop", "movie"),
                null, false, null, null,
                100, null, "access-token", "refresh-token",
                null, null, false, true, false
        );

        when(onBoardingService.onboard(any(OnBoardingRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.university").value("sju"))
                .andExpect(jsonPath("$.gender").value("male"))
                .andExpect(jsonPath("$.isKorean").value(false))
                .andExpect(jsonPath("$.majors[0]").value("humanities"))
                .andExpect(jsonPath("$.languages[1]").value("en"))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }
}
