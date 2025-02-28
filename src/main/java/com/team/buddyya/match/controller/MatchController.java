package com.team.buddyya.match.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.dto.response.MatchCreateResponse;
import com.team.buddyya.match.dto.response.MatchStatusResponse;
import com.team.buddyya.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchCreateResponse> requestMatch(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestBody MatchCreateRequest request) {
        MatchCreateResponse response = matchService.requestMatch(userDetails.getStudentInfo().id(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<MatchStatusResponse> findMatchStatus(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MatchStatusResponse response = matchService.findMatchStatus(userDetails.getStudentInfo().id());
        return ResponseEntity.ok(response);
    }
}
