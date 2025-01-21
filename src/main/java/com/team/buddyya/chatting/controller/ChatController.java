package com.team.buddyya.chatting.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.chatting.dto.request.ChatImageRequest;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.ChatMessageListResponse;
import com.team.buddyya.chatting.dto.response.ChatroomResponse;
import com.team.buddyya.chatting.dto.response.CreateChatroomResponse;
import com.team.buddyya.chatting.dto.response.LeaveChatroomResponse;
import com.team.buddyya.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction;

@RestController
@RequestMapping("/chatrooms")
@RequiredArgsConstructor
public class ChatController {

    private static final int CHAT_MESSAGE_PAGE_SIZE = 15;

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<CreateChatroomResponse> createOrGetChatRoom(@RequestBody CreateChatroomRequest request,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        CreateChatroomResponse response = chatService.createOrGetChatRoom(request, userDetails.getStudentInfo());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ChatroomResponse>> getChatRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.getChatRooms(userDetails.getStudentInfo()));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatMessageListResponse> getChatMessages(
            @PathVariable("roomId") Long chatroomId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = CHAT_MESSAGE_PAGE_SIZE, sort = "createdDate", direction = Direction.DESC) Pageable pageable) {
        ChatMessageListResponse response = chatService.getChatMessages(chatroomId, userDetails.getStudentInfo(), pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<LeaveChatroomResponse> leaveChatroom(
            @PathVariable("roomId") Long chatroomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.leaveChatroom(chatroomId, userDetails.getStudentInfo()));
    }


    @PostMapping("/{roomId}/images")
    public ResponseEntity<Void> uploadImages(@PathVariable("roomId") Long chatroomId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails,
                                             @ModelAttribute ChatImageRequest request) {
        chatService.chatUploadImages(chatroomId, userDetails.getStudentInfo(), request);
        return ResponseEntity.ok().build();
    }
}
