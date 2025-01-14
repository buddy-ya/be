package com.team.buddyya.chatting.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<CreateChatroomResponse> createOrGetChatRoom(@RequestBody CreateChatroomRequest request,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        CreateChatroomResponse response = chatService.createOrGetChatRoom(request, userDetails.getStudentInfo());
        return ResponseEntity.ok(response);
    }
}
