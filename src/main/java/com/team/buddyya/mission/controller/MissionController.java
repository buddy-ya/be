package com.team.buddyya.mission.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.mission.service.MissionService;
import com.team.buddyya.mission.dto.PointMissionResponse;
import com.team.buddyya.mission.dto.PointMissionRewardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    public ResponseEntity<PointMissionResponse> getUserMissionInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        PointMissionResponse response = missionService.getUserMissionInfo(userDetails.getStudentInfo());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/attendance")
    public ResponseEntity<PointMissionRewardResponse> rewardVisitPoint(@AuthenticationPrincipal CustomUserDetails userDetails){
        PointMissionRewardResponse response = missionService.checkAttendanceAndReward(userDetails.getStudentInfo());
        return ResponseEntity.ok(response);
    }
}
