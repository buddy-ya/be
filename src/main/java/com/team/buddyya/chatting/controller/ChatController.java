package com.team.buddyya.chatting.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.chatting.domain.ChatroomType;
import com.team.buddyya.chatting.dto.request.CreateChatroomRequest;
import com.team.buddyya.chatting.dto.response.*;
import com.team.buddyya.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class ChatController {

    private static final int CHAT_MESSAGE_PAGE_SIZE = 15;

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<CreateChatroomResponse> createOrGetChatRoom(@RequestBody CreateChatroomRequest request,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        CreateChatroomResponse response = chatService.createOrGetChatRoom(request, userDetails.getStudentInfo(), ChatroomType.MATCHING);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ChatroomListResponse> getChatRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.getChatRooms(userDetails.getStudentInfo()));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatroomDetailResponse> getChatRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @PathVariable("roomId") Long roomId) {
        return ResponseEntity.ok(chatService.getChatroom(userDetails.getStudentInfo(), roomId));
    }

    @GetMapping("/{roomId}/chats")
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

//    @PostMapping("/{roomId}/image")
//    public ResponseEntity<Void> uploadImages(@PathVariable("roomId") Long chatroomId,
//                                             @AuthenticationPrincipal CustomUserDetails userDetails,
//                                             @ModelAttribute ChatImageRequest request) {
//        chatService.chatUploadImage(chatroomId, userDetails.getStudentInfo(), request);
//        return ResponseEntity.ok().build();
//    }
}
