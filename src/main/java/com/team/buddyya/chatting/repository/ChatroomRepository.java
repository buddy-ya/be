package com.team.buddyya.chatting.repository;

import com.team.buddyya.chatting.domain.Chatroom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("SELECT c FROM Chatroom c " +
            "JOIN c.chatroomStudents s1 ON s1.student.id = :userId " +
            "JOIN c.chatroomStudents s2 ON s2.student.id = :buddyId")
    Optional<Chatroom> findByUserAndBuddy(@Param("userId") Long userId, @Param("buddyId") Long buddyId);

    @Query("SELECT c FROM Chatroom c " +
            "JOIN c.chatroomStudents cs " +
            "WHERE cs.student.id IN (:senderId, :receiverId) " +
            "  AND cs.isExited = false " +
            "GROUP BY c.id " +
            "HAVING COUNT(cs) = 2 " +
            "ORDER BY c.lastMessageTime DESC")
    Optional<Chatroom> findLatestActiveChatroomByUserPair(@Param("senderId") Long senderId,
                                                          @Param("receiverId") Long receiverId);

    Optional<Chatroom> findById(Long chatroomId);
}
