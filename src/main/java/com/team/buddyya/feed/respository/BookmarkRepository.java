package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.Bookmark;
import com.team.buddyya.student.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByStudentIdAndFeedId(Long studentId, Long feedId);

    boolean existsByStudentIdAndFeedId(Long studentId, Long feedId);

    Page<Bookmark> findAllByStudent(Student student, Pageable pageable);
}
