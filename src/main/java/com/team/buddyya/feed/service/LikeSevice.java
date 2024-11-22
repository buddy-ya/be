package com.team.buddyya.feed.service;

import com.team.buddyya.feed.respository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeSevice {

    private final LikeRepository likeRepository;

    public boolean isLikedByStudent(Long studentId, Long feedId) {
        return likeRepository.existsByStudentIdAndFeedId(studentId, feedId);
    }
}
