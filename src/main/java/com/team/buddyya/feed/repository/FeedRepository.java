package com.team.buddyya.feed.repository;

import com.team.buddyya.feed.domain.Category;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom {

    @Override
    @EntityGraph(attributePaths = {
            "category",
            "university",
            "images"
    })
    Optional<Feed> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Feed f where f.id = :id")
    Optional<Feed> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {
            "category",
            "university",
    })
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
