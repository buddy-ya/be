package com.team.buddyya.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.chatting.domain.Chat;
import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;
import com.team.buddyya.chatting.domain.MessageType;
import com.team.buddyya.chatting.dto.request.ChatImageRequest;
import com.team.buddyya.chatting.dto.request.ChatMessage;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.*;
import com.team.buddyya.chatting.exception.ChatException;
import com.team.buddyya.chatting.exception.ChatExceptionType;
import com.team.buddyya.chatting.repository.ChatRepository;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.chatting.repository.ChatroomStudentRepository;
import com.team.buddyya.common.service.S3UploadService;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.team.buddyya.common.domain.S3DirectoryName.CHAT_IMAGE;
import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;

@Slf4j
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
    private final ChatroomStudentRepository chatroomStudentRepository;
    private final Map<Long, Set<WebSocketSession>> sessionsPerRoom = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Long> lastPongTimestamps = new ConcurrentHashMap<>();

    public CreateChatroomResponse createOrGetChatRoom(CreateChatroomRequest request, StudentInfo studentInfo) {
        Student user = findStudentService.findByStudentId(studentInfo.id());
        Student buddy = findStudentService.findByStudentId(request.buddyId());
        Optional<Chatroom> existingRoom = chatRoomRepository.findByUserAndBuddy(user.getId(), buddy.getId());
        if (existingRoom.isPresent()) {
            Chatroom room = existingRoom.get();
            return CreateChatroomResponse.from(room, buddy, false);
        }
        Chatroom newChatroom = createChatroom(user, buddy);
        return CreateChatroomResponse.from(newChatroom, buddy, true);
    }

    private Chatroom createChatroom(Student user, Student buddy) {
        Chatroom newChatroom = Chatroom
                .builder()
                .createdTime(LocalDateTime.now())
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
                log.info("Room ID {}에서 모든 세션이 종료되어 방 제거 완료.", roomId);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ChatroomsResponse> getChatRooms(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        return student.getChatroomStudents().stream()
                .filter(chatroomStudent -> !chatroomStudent.getIsExited())
                .map(chatroomStudent -> createChatroomResponse(chatroomStudent))
                .filter(Objects::nonNull)
                .sorted((a, b) -> b.lastMessageDate().compareTo(a.lastMessageDate()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatroomResponse getChatroom(StudentInfo studentInfo, Long roomId) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        Chatroom chatroom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
        ChatroomStudent chatroomStudent = chatroomStudentRepository.findByChatroomAndStudentId(chatroom, student.getId())
                .orElseThrow(() -> new ChatException(ChatExceptionType.USER_NOT_PART_OF_CHATROOM));
        Student buddy = getBuddyFromChatroom(student, chatroom);
        if (buddy == null) {
            return ChatroomResponse.from(roomId, null, null, true);
        }
        boolean isBuddyExited = chatroom.getChatroomStudents().stream()
                .filter(cs -> !cs.getStudent().getId().equals(student.getId()))
                .anyMatch(ChatroomStudent::getIsExited);
        String buddyProfileImage = getChatroomProfileImage(buddy);
        return ChatroomResponse.from(roomId, buddy.getName(), buddyProfileImage, isBuddyExited);
    }

    private ChatroomsResponse createChatroomResponse(ChatroomStudent chatroomStudent) {
        Chatroom chatroom = chatroomStudent.getChatroom();
        Student buddy = getBuddyFromChatroom(chatroomStudent.getStudent(), chatroom);
        if (buddy == null) {
            return ChatroomsResponse.from(chatroom, null, chatroomStudent, null, true);
        }
        boolean isBuddyExited = chatroom.getChatroomStudents().stream()
                .filter(cs -> !cs.getStudent().getId().equals(chatroomStudent.getStudent().getId()))
                .anyMatch(ChatroomStudent::getIsExited);
        String buddyProfileImage = getChatroomProfileImage(buddy);
        return ChatroomsResponse.from(chatroom, buddy.getName(), chatroomStudent, buddyProfileImage, isBuddyExited);
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
        chatroomStudent.resetUnreadCount();
        Page<Chat> chats = chatRepository.findByChatroom(chatroom, pageable);
        Page<ChatMessageResponse> chatResponses = chats.map(ChatMessageResponse::from);
        return ChatMessageListResponse.from(chatResponses);
    }

    public LeaveChatroomResponse leaveChatroom(Long chatroomId, StudentInfo studentInfo) {
        Chatroom chatroom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHATROOM_NOT_FOUND));
        ChatroomStudent chatroomStudent = chatroomStudentRepository.findByChatroomAndStudentId(chatroom, studentInfo.id())
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
        log.info("Room ID {}에 새로운 세션 추가. 현재 세션 수: {}", roomId, sessionsPerRoom.get(roomId).size());
    }

    //    @Scheduled(fixedRate = 30000)
    public void sendPingMessages() {
        log.info("모든 WebSocket 세션에 Ping 메시지를 전송합니다.");
        long currentTime = System.currentTimeMillis();
        sessionsPerRoom.forEach((roomId, sessions) -> {
            Set<WebSocketSession> invalidSessions;
            try {
                invalidSessions = handlePingAndValidateSessions(roomId, sessions, currentTime);
            } catch (IOException e) {
                log.error("PING 메시지 처리 중 에러 발생: {}", e.getMessage());
                return;
            }
            cleanupInvalidSessions(roomId, sessions, invalidSessions);
        });
    }

    private Set<WebSocketSession> handlePingAndValidateSessions(Long roomId, Set<WebSocketSession> sessions, long currentTime) throws IOException {
        Set<WebSocketSession> invalidSessions = new HashSet<>();
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                invalidSessions.add(session);
                continue;
            }
            session.sendMessage(new TextMessage("PING"));
            if (isSessionTimedOut(session, currentTime)) {
                invalidSessions.add(session);
                log.warn("Room ID {}의 세션 {}이 만료로 제거됩니다.", roomId, session.getId());
            }
        }
        return invalidSessions;
    }

    private boolean isSessionTimedOut(WebSocketSession session, long currentTime) {
        Long timeout = (Long) session.getAttributes().get("timeout");
        return timeout == null || currentTime > timeout;
    }

    private void cleanupInvalidSessions(Long roomId, Set<WebSocketSession> sessions, Set<WebSocketSession> invalidSessions) {
        if (!invalidSessions.isEmpty()) {
            invalidSessions.forEach(session -> closeAndRemoveSession(session));
            sessions.removeAll(invalidSessions);
            if (sessions.isEmpty()) {
                sessionsPerRoom.remove(roomId);
                log.info("Room ID {}에서 모든 세션이 종료되어 방 제거 완료.", roomId);
            }
            log.info("Room ID {}에서 {}개의 비정상 세션 제거 완료.", roomId, invalidSessions.size());
        }
    }

    private void closeAndRemoveSession(WebSocketSession session) {
        try {
            session.close();
        } catch (IOException e) {
            log.error("세션 {} 닫기 실패. 에러: {}", session.getId(), e.getMessage());
        }
        lastPongTimestamps.remove(session);
    }

    public void updateLastPongTimestamp(WebSocketSession session) {
        session.getAttributes().put("timeout", System.currentTimeMillis() + PONG_TIMEOUT);
        log.info("세션 {}으로부터 PONG 수신. 만료 시간 갱신 완료.", session.getId());
    }
}
