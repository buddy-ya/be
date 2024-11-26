package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.student.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

    Page<Feed> findAllByCategoryName(String category, Pageable pageable);

    Page<Feed> findAllByStudent(Student student, Pageable pageable);

    Page<Feed> findByTitleContainingOrContentContaining(String titleQuery, String contentQuery, Pageable pageable);
}
