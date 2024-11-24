package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.FeedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {
}
