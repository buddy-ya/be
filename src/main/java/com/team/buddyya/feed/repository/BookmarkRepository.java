package com.team.buddyya.feed.repository;

import com.team.buddyya.feed.domain.Bookmark;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.student.domain.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByStudentAndFeed(Student student, Feed feed);

    List<Bookmark> findAllByStudentAndFeed(Student student, Feed feed);

    boolean existsByStudentAndFeed(Student student, Feed feed);

    Page<Bookmark> findAllByStudent(Student student, Pageable pageable);
}
