package com.team.buddyya.feed.repository;

import com.team.buddyya.feed.domain.Category;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

    Page<Feed> findAllByUniversityAndCategory(University university, Category category, Pageable pageable);

    Page<Feed> findAllByStudent(Student student, Pageable pageable);

    Page<Feed> findByTitleContainingOrContentContainingAndUniversityIn(
            String titleQuery,
            String contentQuery,
            List<University> universities,
            Pageable pageable
    );

    Page<Feed> findByLikeCountGreaterThanEqualAndUniversity(
            int likeCount,
            University university,
            Pageable pageable
    );
}
