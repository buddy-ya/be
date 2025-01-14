package com.team.buddyya.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.chatting.domain.Chat;
import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;
import com.team.buddyya.chatting.domain.MessageType;
import com.team.buddyya.chatting.dto.request.ChatMessage;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.repository.ChatRepository;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.chatting.repository.ChatroomStudentRepository;
import com.team.buddyya.student.domain.Student;
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
    private final ChatRepository chatRepository;
    private final ChatroomStudentRepository chatroomStudentRepository;
    private final Map<Long, Set<WebSocketSession>> sessionsPerRoom = new HashMap<>();

    public CreateChatroomResponse createOrGetChatRoom(CreateChatroomRequest request, StudentInfo studentInfo) {
        Student user = findStudentService.findByStudentId(studentInfo.id());
        Student buddy = findStudentService.findByStudentId(request.buddyId());
        Optional<Chatroom> existingRoom = chatRoomRepository.findByUserAndBuddyAndPostId(
                studentInfo.id(), request.buddyId(), request.postId());
        if (existingRoom.isPresent()) {
            Chatroom room = existingRoom.get();
            return CreateChatroomResponse.from(room, buddy);
        }
        Chatroom newChatroom = createChatroom(request, user, buddy);
        return CreateChatroomResponse.from(newChatroom, buddy);
    }

    private Chatroom createChatroom(CreateChatroomRequest request, Student user, Student buddy) {
        Chatroom newChatroom = Chatroom.builder()
                .postId(request.postId())
                .name(request.postName())
                .build();
        chatRoomRepository.save(newChatroom);
        ChatroomStudent userChatroom = ChatroomStudent.builder()
                .student(user)
                .chatroom(newChatroom)
                .build();
        ChatroomStudent buddyChatroom = ChatroomStudent.builder()
                .student(buddy)
                .chatroom(newChatroom)
                .build();
        chatroomStudentRepository.save(userChatroom);
        chatroomStudentRepository.save(buddyChatroom);
        return newChatroom;
    }

    public void handleAction(WebSocketSession session, ChatMessage chatMessage) throws IllegalArgumentException {
        Chatroom chatRoom = chatRoomRepository.findById(chatMessage.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        if (chatMessage.getType().equals(MessageType.ENTER)) {
            sessionsPerRoom.computeIfAbsent(chatRoom.getId(), k -> new HashSet<>()).add(session);
        } else if (chatMessage.getType().equals(MessageType.TALK)) {
            saveMessage(chatMessage);
            sendMessageToRoom(chatRoom.getId(), chatMessage, session);
        }
    }

    public void saveMessage(ChatMessage chatMessage) {
        Student sender = findStudentService.findByStudentId(chatMessage.getUserId());
        Chatroom chatroom = chatRoomRepository.findById(chatMessage.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        Chat chat = Chat.builder()
                .chatroom(chatroom)
                .student(sender)
                .message(chatMessage.getMessage())
                .build();
        chatRepository.save(chat);
    }

    public void sendMessageToRoom(Long roomId, ChatMessage message, WebSocketSession senderSession) {
        Set<WebSocketSession> sessions = sessionsPerRoom.get(roomId);
        if (sessions != null) {
            sessions.removeIf(session -> !session.equals(senderSession) && !sendMessage(session, message));
        }
    }

    public boolean sendMessage(WebSocketSession session, ChatMessage message) {
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void removeSession(WebSocketSession session) {
        sessionsPerRoom.values().forEach(sessions -> sessions.remove(session));
    }
}
