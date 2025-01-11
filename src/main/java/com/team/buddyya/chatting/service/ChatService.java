package com.team.buddyya.chatting.service;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.chatting.repository.ChatroomStudentRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatroomRepository chatRoomRepository;
    private final FindStudentService findStudentService;
    private final StudentRepository studentRepository;
    private final ChatroomStudentRepository chatroomStudentRepository;

    public CreateChatroomResponse createOrGetChatRoom(CreateChatroomRequest request) {
        Student student = findStudentService.findByStudentId(request.userId());
        Student buddy = findStudentService.findByStudentId(request.buddyId());

        // 이미 존재하는 채팅방 확인
        Optional<Chatroom> existingRoom = chatroomStudentRepository.findByStudents(student, buddy)
                .map(ChatroomStudent::getChatroom);

        if (existingRoom.isPresent()) {
            return new CreateChatroomResponse(existingRoom.get().getId());
        }

        // 새로운 채팅방 생성
        Chatroom newChatroom = new Chatroom();
        chatRoomRepository.save(newChatroom);

        // StudentChatRoom 엔티티 생성 및 저장
        ChatroomStudent chatRoomStudent = ChatroomStudent.builder()
                .student(student)
                .chatroom(newChatroom)
                .build();
        ChatroomStudent buddyChatRoom = ChatroomStudent.builder()
                .student(buddy)
                .chatroom(newChatroom)
                .build();

        chatroomStudentRepository.save(chatRoomStudent);
        chatroomStudentRepository.save(buddyChatRoom);

        return new CreateChatroomResponse(newChatroom.getId());
    }
}
