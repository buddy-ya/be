package com.team.buddyya.chatting.service;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.chatting.domain.ChatRequest;
import com.team.buddyya.chatting.dto.response.ChatRequestResponse;
import com.team.buddyya.chatting.exception.ChatException;
import com.team.buddyya.chatting.exception.ChatExceptionType;
import com.team.buddyya.chatting.repository.ChatRequestRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRequestService {

    private final ChatRequestRepository chatRequestRepository;
    private final FindStudentService findStudentService;

    @Transactional(readOnly = true)
    public List<ChatRequestResponse> getChatRequests(CustomUserDetails userDetails) {
        Student receiver = findStudentService.findByStudentId(userDetails.getStudentInfo().id());
        List<ChatRequest> chatRequests = chatRequestRepository.findAllByReceiver(receiver);
        return chatRequests.stream()
                .map(ChatRequestResponse::from)
                .toList();
    }

    public void createChatRequest(CustomUserDetails userDetails, Long receiverId) {
        Student sender = findStudentService.findByStudentId(userDetails.getStudentInfo().id());
        Student receiver = findStudentService.findByStudentId(receiverId);
        if (sender.getId().equals(receiverId)) {
            throw new ChatException(ChatExceptionType.SELF_CHAT_REQUEST_NOT_ALLOWED);
        }
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
}
