package com.team.buddyya.chatting.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.ChatRoomResponse;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> readChatRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.readChatRooms(userDetails.getStudentInfo()));
    }
}
