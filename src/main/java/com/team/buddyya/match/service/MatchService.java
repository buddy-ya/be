package com.team.buddyya.match.service;

import com.team.buddyya.match.dto.request.MatchCreateRequest;
import com.team.buddyya.match.dto.response.MatchDeleteResponse;
import com.team.buddyya.match.dto.response.MatchResponse;
import com.team.buddyya.match.dto.response.MatchStatusResponse;

public interface MatchService {

    MatchResponse requestMatch(Long studentId, MatchCreateRequest request);

    MatchDeleteResponse deleteMatch(Long studentId);

    MatchStatusResponse findMatchStatus(Long studentId);
}
