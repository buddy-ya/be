package com.team.buddyya.chatting.controller;

import static com.team.buddyya.chatting.domain.ChatroomType.CHAT_REQUEST;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.ChatRequestResponse;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.service.ChatRequestService;
import com.team.buddyya.chatting.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat-requests")
@RequiredArgsConstructor
public class ChatRequestController {

    private final ChatRequestService chatRequestService;
    private final ChatService chatService;

//    @GetMapping("/{receiverId}")
//    public ResponseEntity<ChatRequestInfoResponse> getChatRequestInfo(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @PathVariable Long receiverId) {
//        return ResponseEntity.ok(chatRequestService.getChatRequestInfo(userDetails, receiverId));
//    }

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

    @PostMapping
    public ResponseEntity<CreateChatroomResponse> createOrGetChatRoom(@RequestBody CreateChatroomRequest request,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        CreateChatroomResponse response = chatService.createOrGetChatRoom(request, userDetails.getStudentInfo(),
                CHAT_REQUEST);
        chatRequestService.deleteChatRequest(request.chatRequestId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{chatRequestId}")
    public ResponseEntity<Void> deleteChatRequest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable("chatRequestId") Long chatRequestId) {
        chatRequestService.deleteChatRequest(chatRequestId);
        return ResponseEntity.noContent().build();
    }
}
