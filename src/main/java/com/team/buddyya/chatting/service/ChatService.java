package com.team.buddyya.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.chatting.domain.Chat;
import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;
import com.team.buddyya.chatting.domain.MessageType;
import com.team.buddyya.chatting.dto.request.ChatRequest;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.repository.ChatRepository;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.chatting.repository.ChatroomStudentRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ObjectMapper mapper;
    private final ChatroomRepository chatRoomRepository;
    private final FindStudentService findStudentService;
    private final StudentRepository studentRepository;
    private final ChatRepository chatRepository;
    private final ChatroomStudentRepository chatroomStudentRepository;
    private final Map<Long, Set<WebSocketSession>> sessionsPerRoom = new HashMap<>();

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

    public void handleAction(WebSocketSession session, ChatRequest chatRequest) {
        Chatroom chatRoom = chatRoomRepository.findById(chatRequest.roomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Student student = studentRepository.findById(chatRequest.studentId())
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        if (chatRequest.type().equals(MessageType.ENTER)) {
            sessionsPerRoom.computeIfAbsent(chatRoom.getId(), k -> new HashSet<>()).add(session);
            sendMessageToRoom(chatRoom.getId(), chatRequest);
        } else if (chatRequest.type().equals(MessageType.TALK)) {
            saveMessage(chatRequest);
            sendMessageToRoom(chatRoom.getId(), chatRequest);
        }
    }

    public void saveMessage(ChatRequest chatRequest) {
        Student sender = findStudentService.findByStudentId(chatRequest.studentId());
        Chatroom chatroom = chatRoomRepository.findById(chatRequest.roomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Chat chat = Chat.builder()
                .chatroom(chatroom)
                .student(sender)
                .message(chatRequest.message())
                .build();
        chatRepository.save(chat);
    }

    public <T> void sendMessageToRoom(Long roomId, T message) {
        Set<WebSocketSession> sessions = sessionsPerRoom.get(roomId);
        if (sessions != null) {
            sessions.parallelStream().forEach(session -> sendMessage(session, message));
        }
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
