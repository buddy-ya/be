package com.team.buddyya.point.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.point.dto.PointListResponse;
import com.team.buddyya.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping
    public ResponseEntity<PointListResponse> getPoints(@AuthenticationPrincipal CustomUserDetails userDetails) {
        PointListResponse response = pointService.getPoints(userDetails.getStudentInfo());
        return ResponseEntity.ok(response);
    }
}
