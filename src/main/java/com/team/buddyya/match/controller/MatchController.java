package com.team.buddyya.match.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.dto.response.MatchDeleteResponse;
import com.team.buddyya.match.dto.response.MatchResponse;
import com.team.buddyya.match.dto.response.MatchStatusResponse;
import com.team.buddyya.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchResponse> requestMatch(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @RequestBody MatchCreateRequest request) {
        MatchResponse response = matchService.requestMatch(userDetails.getStudentInfo().id(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<MatchDeleteResponse> deleteMatch(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MatchDeleteResponse response = matchService.deleteMatch(userDetails.getStudentInfo().id());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<MatchStatusResponse> findMatchStatus(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MatchStatusResponse response = matchService.findMatchStatus(userDetails.getStudentInfo().id());
        return ResponseEntity.ok(response);
    }
}
