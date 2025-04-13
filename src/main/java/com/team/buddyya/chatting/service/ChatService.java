package com.team.buddyya.chatting.service;

import static com.team.buddyya.common.domain.S3DirectoryName.CHAT_IMAGE;
import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.chatting.domain.Chat;
import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;
import com.team.buddyya.chatting.domain.ChatroomType;
import com.team.buddyya.chatting.domain.MessageType;
import com.team.buddyya.chatting.dto.request.ChatImageRequest;
import com.team.buddyya.chatting.dto.request.ChatMessage;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.ChatMessageListResponse;
import com.team.buddyya.chatting.dto.response.ChatMessageResponse;
import com.team.buddyya.chatting.dto.response.ChatroomDetailResponse;
import com.team.buddyya.chatting.dto.response.ChatroomListResponse;
import com.team.buddyya.chatting.dto.response.ChatroomResponse;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.dto.response.LeaveChatroomResponse;
import com.team.buddyya.chatting.exception.ChatException;
import com.team.buddyya.chatting.exception.ChatExceptionType;
import com.team.buddyya.chatting.repository.ChatRepository;
import com.team.buddyya.chatting.repository.ChatRequestRepository;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.chatting.repository.ChatroomStudentRepository;
import com.team.buddyya.common.service.S3UploadService;
import com.team.buddyya.notification.service.NotificationService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private static final String ERROR_CHATROOM_NOT_FOUND = "채팅방을 찾을 수 없습니다.";
    private static final String CHATROOM_LEAVE_SUCCESS_MESSAGE = "채팅방을 나갔습니다.";
    private static final String IMAGE_SENT_MESSAGE = "사진을 보냈습니다";
    private static final long PONG_TIMEOUT = 45000;

    private final ObjectMapper mapper;
    private final FindStudentService findStudentService;
    private final S3UploadService s3UploadService;
    private final ChatRepository chatRepository;
    private final ChatroomRepository chatRoomRepository;
    private final ChatRequestRepository chatRequestRepository;
    private final ChatroomStudentRepository chatroomStudentRepository;
    private final Map<Long, Set<WebSocketSession>> sessionsPerRoom = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Long> lastPongTimestamps = new ConcurrentHashMap<>();
    private final NotificationService notificationService;

    public CreateChatroomResponse createOrGetChatRoom(CreateChatroomRequest request, StudentInfo studentInfo,
                                                      ChatroomType chatroomType) {
        Student user = findStudentService.findByStudentId(studentInfo.id());
        Student buddy = findStudentService.findByStudentId(request.buddyId());
        Chatroom newChatroom = createChatroom(user, buddy, chatroomType);
        notificationService.sendChatAcceptNotification(buddy, user.getName(), newChatroom.getId());
        return CreateChatroomResponse.from(newChatroom, buddy, true);
    }

    public Chatroom createChatroom(Student user, Student buddy, ChatroomType type) {
        Chatroom newChatroom = Chatroom
                .builder()
                .createdTime(LocalDateTime.now())
                .type(type)
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

    @Transactional(readOnly = true)
    public Chatroom findByChatroomByChatroomId(Long chatroomId) {
        return chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
    }

    public void handleAction(ChatMessage chatMessage) throws IllegalArgumentException {
        Chatroom chatRoom = chatRoomRepository.findById(chatMessage.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException(ERROR_CHATROOM_NOT_FOUND));
        if (chatMessage.getType().equals(MessageType.TALK)) {
            saveMessageAndHandleUnreadCount(chatMessage);
            sendMessageToRoom(chatRoom.getId(), chatMessage);
        }
    }

    private void saveMessageAndHandleUnreadCount(ChatMessage chatMessage) {
        Chatroom chatroom = chatRoomRepository.findById(chatMessage.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException(ERROR_CHATROOM_NOT_FOUND));
        Student sender = findStudentService.findByStudentId(chatMessage.getUserId());
        Chat chat = Chat.builder()
                .type(chatMessage.getType())
                .chatroom(chatroom)
                .student(sender)
                .message(chatMessage.getMessage())
                .build();
        String lastMessageContent = chatMessage.getType() == MessageType.IMAGE
                ? IMAGE_SENT_MESSAGE : chatMessage.getMessage();
        chatroom.updateLastMessage(lastMessageContent);
        chatRepository.save(chat);
        updateUnreadCountForChatroom(chatroom, sender.getId());
    }

    private void updateUnreadCountForChatroom(Chatroom chatroom, Long senderId) {
        Set<WebSocketSession> sessions = sessionsPerRoom.getOrDefault(chatroom.getId(), Collections.emptySet());
        for (ChatroomStudent chatroomStudent : chatroom.getChatroomStudents()) {
            Long receiverId = chatroomStudent.getStudent().getId();
            boolean isReceiverConnected = sessions.stream()
                    .anyMatch(session -> receiverId.equals(session.getAttributes().get("userId")));
            if (!senderId.equals(receiverId) && !isReceiverConnected) {
                chatroomStudent.increaseUnreadCount();
                chatroomStudentRepository.save(chatroomStudent);
            }
        }
    }

    public void sendMessageToRoom(Long roomId, ChatMessage message) {
        Set<WebSocketSession> sessions = sessionsPerRoom.get(roomId);
        if (sessions != null) {
            sessions.removeIf(session -> !sendMessage(session, message));
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
        Long roomId = (Long) session.getAttributes().get("roomId");
        Set<WebSocketSession> sessions = sessionsPerRoom.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            lastPongTimestamps.remove(session);
            if (sessions.isEmpty()) {
                sessionsPerRoom.remove(roomId);
            }
        }
    }

    @Transactional(readOnly = true)
    public ChatroomListResponse getChatRooms(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        List<ChatroomResponse> chatroomResponses = student.getChatroomStudents().stream()
                .filter(chatroomStudent -> !chatroomStudent.getIsExited())
                .map(chatroomStudent -> createChatroomResponse(chatroomStudent))
                .filter(Objects::nonNull)
                .sorted((a, b) -> b.lastMessageDate().compareTo(a.lastMessageDate()))
                .collect(Collectors.toList());
        int totalUnreadCount = (int) chatroomResponses.stream()
                .filter(chatroomResponse -> chatroomResponse.unreadCount() > 0)
                .count();
        boolean hasChatRequest = chatRequestRepository.existsByReceiver(student);
        return ChatroomListResponse.from(chatroomResponses, totalUnreadCount, hasChatRequest);
    }

    @Transactional(readOnly = true)
    public ChatroomDetailResponse getChatroom(StudentInfo studentInfo, Long roomId) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        Chatroom chatroom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
        ChatroomStudent chatroomStudent = chatroomStudentRepository.findByChatroomAndStudentId(chatroom,
                        student.getId())
                .orElseThrow(() -> new ChatException(ChatExceptionType.USER_NOT_PART_OF_CHATROOM));
        Student buddy = getBuddyFromChatroom(student, chatroom);
        if (buddy == null) {
            return ChatroomDetailResponse.from(roomId, null, null, null, null, true, chatroom.getType().name());
        }
        boolean isBuddyExited = chatroom.getChatroomStudents().stream()
                .filter(cs -> !cs.getStudent().getId().equals(student.getId()))
                .anyMatch(ChatroomStudent::getIsExited);
        String buddyProfileImage = getChatroomProfileImage(buddy);
        return ChatroomDetailResponse.from(roomId, buddy.getName(), buddy.getCountry(), buddyProfileImage,
                buddy.getId(), isBuddyExited, chatroom.getType().name());
    }

    private ChatroomResponse createChatroomResponse(ChatroomStudent chatroomStudent) {
        Chatroom chatroom = chatroomStudent.getChatroom();
        Student buddy = getBuddyFromChatroom(chatroomStudent.getStudent(), chatroom);
        if (buddy == null) {
            return ChatroomResponse.from(chatroom, null, null, chatroomStudent, null, null, true);
        }
        boolean isBuddyExited = chatroom.getChatroomStudents().stream()
                .filter(cs -> !cs.getStudent().getId().equals(chatroomStudent.getStudent().getId()))
                .anyMatch(ChatroomStudent::getIsExited);
        String buddyProfileImage = getChatroomProfileImage(buddy);
        return ChatroomResponse.from(chatroom, buddy.getName(), buddy.getCountry(), chatroomStudent, buddyProfileImage,
                buddy.getId(), isBuddyExited);
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
        ChatroomStudent chatroomStudent = chatroomStudentRepository.findByChatroomAndStudentId(chatroom,
                        studentInfo.id())
                .orElseThrow(() -> new ChatException(ChatExceptionType.USER_NOT_PART_OF_CHATROOM));
        chatroomStudent.resetUnreadCount();
        Page<Chat> chats = chatRepository.findByChatroom(chatroom, pageable);
        Page<ChatMessageResponse> chatResponses = chats.map(ChatMessageResponse::from);
        return ChatMessageListResponse.from(chatResponses);
    }

    public LeaveChatroomResponse leaveChatroom(Long chatroomId, StudentInfo studentInfo) {
        Chatroom chatroom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
        ChatroomStudent chatroomStudent = chatroomStudentRepository.findByChatroomAndStudentId(chatroom,
                        studentInfo.id())
                .orElseThrow(() -> new ChatException(ChatExceptionType.USER_NOT_PART_OF_CHATROOM));
        chatroomStudent.updateIsExited();
        return LeaveChatroomResponse.from(CHATROOM_LEAVE_SUCCESS_MESSAGE);
    }

    public void chatUploadImage(Long roomId, StudentInfo studentInfo, ChatImageRequest request) {
        String imageUrl = s3UploadService.uploadFile(CHAT_IMAGE.getDirectoryName(), request.image());
        ChatMessage chatMessage = ChatMessage.builder()
                .type(MessageType.IMAGE)
                .roomId(roomId)
                .tempId(request.tempId())
                .message(imageUrl)
                .userId(studentInfo.id())
                .time(LocalDateTime.now())
                .build();
        saveMessageAndHandleUnreadCount(chatMessage);
        sendMessageToRoom(roomId, chatMessage);
    }

    public void addSessionToRoom(Long roomId, WebSocketSession session) {
        sessionsPerRoom.computeIfAbsent(roomId, k -> new HashSet<>()).add(session);
    }

    //    @Scheduled(fixedRate = 30000)
    public void sendPingMessages() {
        long currentTime = System.currentTimeMillis();
        sessionsPerRoom.forEach((roomId, sessions) -> {
            Set<WebSocketSession> invalidSessions;
            try {
                invalidSessions = handlePingAndValidateSessions(roomId, sessions, currentTime);
            } catch (IOException e) {
                return;
            }
            cleanupInvalidSessions(roomId, sessions, invalidSessions);
        });
    }

    private Set<WebSocketSession> handlePingAndValidateSessions(Long roomId, Set<WebSocketSession> sessions,
                                                                long currentTime) throws IOException {
        Set<WebSocketSession> invalidSessions = new HashSet<>();
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                invalidSessions.add(session);
                continue;
            }
            session.sendMessage(new TextMessage("PING"));
            if (isSessionTimedOut(session, currentTime)) {
                invalidSessions.add(session);
            }
        }
        return invalidSessions;
    }

    private boolean isSessionTimedOut(WebSocketSession session, long currentTime) {
        Long timeout = (Long) session.getAttributes().get("timeout");
        return timeout == null || currentTime > timeout;
    }

    private void cleanupInvalidSessions(Long roomId, Set<WebSocketSession> sessions,
                                        Set<WebSocketSession> invalidSessions) {
        if (!invalidSessions.isEmpty()) {
            invalidSessions.forEach(session -> closeAndRemoveSession(session));
            sessions.removeAll(invalidSessions);
            if (sessions.isEmpty()) {
                sessionsPerRoom.remove(roomId);
            }
        }
    }

    private void closeAndRemoveSession(WebSocketSession session) {
        try {
            session.close();
        } catch (IOException e) {
        }
        lastPongTimestamps.remove(session);
    }

    public void updateLastPongTimestamp(WebSocketSession session) {
        session.getAttributes().put("timeout", System.currentTimeMillis() + PONG_TIMEOUT);
    }
}
