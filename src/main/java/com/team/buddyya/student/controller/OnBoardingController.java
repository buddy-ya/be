package com.team.buddyya.student.controller;

import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.dto.response.OnBoardingResponse;
import com.team.buddyya.student.service.OnBoardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
public class OnBoardingController {

    private final OnBoardingService onBoardingService;

    @PostMapping
    public ResponseEntity<OnBoardingResponse> onboard(@RequestBody OnBoardingRequest request) {
        OnBoardingResponse response = onBoardingService.onboard(request);
        return ResponseEntity.ok(response);
    }
}