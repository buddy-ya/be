package com.team.buddyya.auth.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.auth.dto.request.TokenReissueRequest;
import com.team.buddyya.auth.dto.response.TokenReissueResponse;
import com.team.buddyya.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<TokenReissueResponse> reissueToken(@RequestBody TokenReissueRequest request) {
        TokenReissueResponse tokenResponse = authService.reissueToken(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/success")
    public ResponseEntity<String> success(@AuthenticationPrincipal CustomUserDetails userDetails) {
        StudentInfo studentInfo = userDetails.getStudentInfo();
        return ResponseEntity.ok(studentInfo.toString());
    }

    @GetMapping("/fail")
    public ResponseEntity<String> fail(@AuthenticationPrincipal CustomUserDetails userDetails) {
        StudentInfo studentInfo = userDetails.getStudentInfo();
        return ResponseEntity.ok(studentInfo.toString());
    }
}
