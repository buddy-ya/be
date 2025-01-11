package com.team.buddyya.chatting.repository;

import com.team.buddyya.chatting.domain.ChatroomStudent;
import com.team.buddyya.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatroomStudentRepository extends JpaRepository<ChatroomStudent, Long> {

    @Query("SELECT scr FROM ChatroomStudent scr WHERE scr.student = :student1 AND scr.chatroom IN (SELECT scr2.chatroom FROM ChatroomStudent scr2 WHERE scr2.student = :student2)")
    Optional<ChatroomStudent> findByStudents(@Param("student1") Student student1, @Param("student2") Student student2);
}
