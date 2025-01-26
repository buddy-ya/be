package com.team.buddyya.chatting.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.chatting.dto.response.ChatRequestResponse;
import com.team.buddyya.chatting.service.ChatRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat-request")
@RequiredArgsConstructor
public class ChatRequestController {

    private final ChatRequestService chatRequestService;

    @GetMapping
    public ResponseEntity<List<ChatRequestResponse>> getChatRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatRequestService.getChatRequests(userDetails));
    }

    @PostMapping("/{receiverId}")
    public ResponseEntity<Void> createChatRequest(@AuthenticationPrincipal CustomUserDetails userDetails
            , @PathVariable("receiverId") Long receiverId) {
        chatRequestService.createChatRequest(userDetails, receiverId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{chatRequestId}")
    public ResponseEntity<Void> deleteChatRequest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable("chatRequestId") Long chatRequestId) {
        chatRequestService.deleteChatRequest(userDetails, chatRequestId);
        return ResponseEntity.noContent().build();
    }
}
