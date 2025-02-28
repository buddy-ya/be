package com.team.buddyya.match.service;

import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.dto.response.MatchCreateResponse;
import com.team.buddyya.match.dto.response.MatchStatusResponse;

public interface MatchService {
    MatchCreateResponse requestMatch(Long studentId, MatchCreateRequest request);

    MatchStatusResponse findMatchStatus(Long studentId);
}
