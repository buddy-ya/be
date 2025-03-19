package com.team.buddyya.match.service;

import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.dto.response.MatchDeleteResponse;
import com.team.buddyya.match.dto.response.MatchResponse;

public interface MatchService {

    MatchResponse requestMatch(Long studentId, MatchCreateRequest request);

    MatchDeleteResponse deleteMatch(Long studentId);

    MatchResponse findMatchStatus(Long studentId);
}
