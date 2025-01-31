package com.team.buddyya.chatting.service;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.chatting.domain.ChatRequest;
import com.team.buddyya.chatting.dto.response.ChatRequestResponse;
import com.team.buddyya.chatting.exception.ChatException;
import com.team.buddyya.chatting.exception.ChatExceptionType;
import com.team.buddyya.chatting.repository.ChatRequestRepository;
import com.team.buddyya.chatting.repository.ChatroomRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRequestService {

    private static final long EXPIRED_CHAT_REQUEST_DAY = 1;

    private final ChatRequestRepository chatRequestRepository;
    private final ChatroomRepository chatroomRepository;
    private final FindStudentService findStudentService;

    @Transactional(readOnly = true)
    public List<ChatRequestResponse> getChatRequests(CustomUserDetails userDetails) {
        Student receiver = findStudentService.findByStudentId(userDetails.getStudentInfo().id());
        List<ChatRequest> chatRequests = chatRequestRepository.findAllByReceiver(receiver);
        return chatRequests.stream()
                .map(ChatRequestResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isAlreadyExistChatRequest(Student sender, Student receiver) {
        return chatRequestRepository.existsBySenderAndReceiver(sender, receiver)
                || chatRequestRepository.existsBySenderAndReceiver(receiver, sender);
    }

    @Transactional(readOnly = true)
    public boolean isAlreadyExistChatroom(Student sender, Student receiver) {
        return chatroomRepository.findByUserAndBuddy(sender.getId(), receiver.getId()).isPresent();
    }

    public void createChatRequest(CustomUserDetails userDetails, Long receiverId) {
        Student sender = findStudentService.findByStudentId(userDetails.getStudentInfo().id());
        Student receiver = findStudentService.findByStudentId(receiverId);
        validateChatRequest(sender, receiver);
        ChatRequest chatRequest = ChatRequest
                .builder()
                .sender(sender)
                .receiver(receiver)
                .build();
        chatRequestRepository.save(chatRequest);
    }

    public void deleteChatRequest(CustomUserDetails userDetails, Long chatRequestId) {
        Student sender = findStudentService.findByStudentId(userDetails.getStudentInfo().id());
        chatRequestRepository.deleteById(chatRequestId);
    }

    @Scheduled(fixedRate = 60 * 1000)
    private void deleteExpiredChatRequests() {
        LocalDateTime expirationTime = LocalDateTime.now().minusDays(EXPIRED_CHAT_REQUEST_DAY);
        List<ChatRequest> expiredChatRequests = chatRequestRepository.findAllByCreatedDateBefore(expirationTime);
        // TO DO: 포인트 환급 등 진행
        chatRequestRepository.deleteByCreatedDateBefore(expirationTime);
    }

    private void validateChatRequest(Student sender, Student receiver) {
        if (sender.getId().equals(receiver.getId())) {
            throw new ChatException(ChatExceptionType.SELF_CHAT_REQUEST_NOT_ALLOWED);
        }
        if (isAlreadyExistChatRequest(sender, receiver)) {
            throw new ChatException(ChatExceptionType.CHAT_REQUEST_ALREADY_EXISTS);
        }
        if (isAlreadyExistChatroom(sender, receiver)) {
            throw new ChatException(ChatExceptionType.CHATROOM_ALREADY_EXISTS);
        }
    }
}
