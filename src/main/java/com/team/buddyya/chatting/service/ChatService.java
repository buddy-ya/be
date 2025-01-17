package com.team.buddyya.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.chatting.domain.Chat;
import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;
import com.team.buddyya.chatting.domain.MessageType;
import com.team.buddyya.chatting.dto.request.ChatMessage;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.ChatMessageListResponse;
import com.team.buddyya.chatting.dto.response.ChatMessageResponse;
import com.team.buddyya.chatting.dto.response.ChatRoomResponse;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.exception.ChatException;
import com.team.buddyya.chatting.exception.ChatExceptionType;
import com.team.buddyya.chatting.repository.ChatRepository;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.chatting.repository.ChatroomStudentRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private static final String ERROR_CHATROOM_NOT_FOUND = "채팅방을 찾을 수 없습니다.";

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
                studentInfo.id(), request.buddyId(), request.feedId());
        if (existingRoom.isPresent()) {
            Chatroom room = existingRoom.get();
            return CreateChatroomResponse.from(room, buddy);
        }
        Chatroom newChatroom = createChatroom(request, user, buddy);
        return CreateChatroomResponse.from(newChatroom, buddy);
    }

    private Chatroom createChatroom(CreateChatroomRequest request, Student user, Student buddy) {
        Chatroom newChatroom = Chatroom.builder()
                .postId(request.feedId())
                .name(request.feedName())
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
                .orElseThrow(() -> new IllegalArgumentException(ERROR_CHATROOM_NOT_FOUND));
        if (chatMessage.getType().equals(MessageType.ENTER)) {
            sessionsPerRoom.computeIfAbsent(chatRoom.getId(), k -> new HashSet<>()).add(session);
        } else if (chatMessage.getType().equals(MessageType.TALK)) {
            saveMessageAndHandleUnreadCount(chatMessage, session);
            sendMessageToRoom(chatRoom.getId(), chatMessage, session);
        }
    }

    private void saveMessageAndHandleUnreadCount(ChatMessage chatMessage, WebSocketSession senderSession) {
        Student sender = findStudentService.findByStudentId(chatMessage.getUserId());
        Chatroom chatroom = chatRoomRepository.findById(chatMessage.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException(ERROR_CHATROOM_NOT_FOUND));
        Chat chat = Chat.builder()
                .chatroom(chatroom)
                .student(sender)
                .message(chatMessage.getMessage())
                .build();
        chatroom.updateLastMessage(chatMessage.getMessage());
        chatRepository.save(chat);
        updateUnreadCountForChatroom(chatroom, senderSession);
    }

    private void updateUnreadCountForChatroom(Chatroom chatroom, WebSocketSession senderSession) {
        Set<WebSocketSession> sessions = sessionsPerRoom.getOrDefault(chatroom.getId(), Collections.emptySet());
        Long senderId = (Long) senderSession.getAttributes().get("userId");
        for (ChatroomStudent chatroomStudent : chatroom.getChatroomStudents()) {
            Long receiverId = chatroomStudent.getStudent().getId();
            if (!senderId.equals(receiverId) && !isReceiverConnected(sessions, receiverId)) {
                chatroomStudent.increaseUnreadCount();
                chatroomStudentRepository.save(chatroomStudent);
            }
        }
    }

    private boolean isReceiverConnected(Set<WebSocketSession> sessions, Long receiverId) {
        return sessions.stream()
                .anyMatch(session -> receiverId.equals(session.getAttributes().get("userId")));
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

    public List<ChatRoomResponse> readChatRooms(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        return student.getChatroomStudents().stream()
                .map(chatroomStudent -> createChatroomResponseIfValid(chatroomStudent))
                .filter(Objects::nonNull)
                .sorted((a, b) -> b.lastMessageTime().compareTo(a.lastMessageTime()))
                .collect(Collectors.toList());
    }

    private ChatRoomResponse createChatroomResponseIfValid(ChatroomStudent chatroomStudent) {
        Chatroom chatroom = chatroomStudent.getChatroom();
        LocalDateTime leaveTime = chatroomStudent.getLeaveTime();
        LocalDateTime lastMessageTime = chatroom.getLastMessageTime();
        if (lastMessageTime == null || lastMessageTime.isBefore(leaveTime)) {
            return null;
        }
        Student buddy = getBuddyFromChatroom(chatroomStudent.getStudent(), chatroom);
        if (buddy == null) {
            return ChatRoomResponse.from(chatroom, chatroomStudent, null);
        }
        return ChatRoomResponse.from(chatroom, chatroomStudent, buddy.getProfileImage().getUrl());
    }

    private Student getBuddyFromChatroom(Student student, Chatroom chatroom) {
        return chatroom.getChatroomStudents().stream()
                .map(ChatroomStudent::getStudent)
                .filter(s -> !s.getId().equals(student.getId()))
                .findFirst()
                .orElse(null);
    }

    public ChatMessageListResponse getChatMessages(Long chatroomId, StudentInfo studentInfo, Pageable pageable) {
        Chatroom chatroom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
        ChatroomStudent chatroomStudent = chatroomStudentRepository.findByChatroomAndStudentId(chatroom, studentInfo.id())
                .orElseThrow(() -> new ChatException(ChatExceptionType.USER_NOT_PART_OF_CHATROOM));
        LocalDateTime leaveTime = chatroomStudent.getLeaveTime();
        Page<Chat> chats = chatRepository.findByChatroomAndCreatedDateAfter(chatroom, leaveTime, pageable);
        chatroomStudent.resetUnreadCount();
        Page<ChatMessageResponse> chatResponses = chats.map(ChatMessageResponse::from);
        return ChatMessageListResponse.from(chatResponses);
    }

    public void leaveChatroom(Long chatroomId, StudentInfo studentInfo) {
        Chatroom chatroom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
        ChatroomStudent chatroomStudent = chatroomStudentRepository.findByChatroomAndStudentId(chatroom, studentInfo.id())
                .orElseThrow(() -> new ChatException(ChatExceptionType.USER_NOT_PART_OF_CHATROOM));
        chatroomStudent.updateLeaveTime();
        chatroomStudent.resetUnreadCount();
    }
}
