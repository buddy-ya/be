package com.team.buddyya.match.service;

import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.dto.response.MatchCreateResponse;

public interface MatchService {
    MatchCreateResponse requestMatch(Long studentId, MatchCreateRequest request);
}
