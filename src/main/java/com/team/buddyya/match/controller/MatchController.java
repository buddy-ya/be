package com.team.buddyya.match.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.dto.response.MatchResponse;
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
    public ResponseEntity<Void> deleteMatch(@AuthenticationPrincipal CustomUserDetails userDetails) {
        matchService.deleteMatch(userDetails.getStudentInfo().id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<MatchResponse> findMatchStatus(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MatchResponse response = matchService.findMatchStatus(userDetails.getStudentInfo().id());
        return ResponseEntity.ok(response);
    }
}
