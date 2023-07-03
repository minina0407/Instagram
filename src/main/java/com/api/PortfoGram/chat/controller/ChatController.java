package com.api.PortfoGram.chat.controller;

import com.api.PortfoGram.chat.dto.ChatMessage;
import com.api.PortfoGram.chat.service.ChatMessageService;
import com.api.PortfoGram.chat.service.ChatRoomService;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final RabbitTemplate rabbitTemplate;
    private final ChatMessageService chatMessageService;
    private final UserService userService;

    @PostMapping("/rooms")
    public void createChatRoom(@RequestBody ChatMessage chatMessage) {
        // 사용자 정보로 UserEntity 생성
        UserEntity sender = userService.getMyUserWithAuthorities();
        UserEntity receiver = userService.findById(chatMessage.getReceiverId());

        // 채팅방 생성 로직
        Long chatRoomId = chatRoomService.createNewChatRoom(sender, receiver);

        // RabbitMQ를 통해 채팅방 생성 메시지를 전송
        rabbitTemplate.convertAndSend("chat.room.exchange", "chat.room.created", chatRoomId);
    }

    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<String> joinChatRoom(@PathVariable Long roomId) {

        // 사용자 정보로 UserEntity 생성
        UserEntity user = userService.getMyUserWithAuthorities();

        // 채팅방 입장 로직
        chatRoomService.joinChatRoom(roomId, user);

        // RabbitMQ를 통해 채팅방 입장 메시지를 전송
        rabbitTemplate.convertAndSend("chat.room.exchange", "chat.room.join", roomId);

        return ResponseEntity.ok("Joined chat room successfully");
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<String> sendMessage(@PathVariable Long roomId, @RequestBody String message) {
        if (roomId == null) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "채팅방이 생성되지 않았습니다.");
        }

        if (message.isEmpty()) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_MISSING, "메세지를 입력해주세요");
        }
        // 사용자 정보로 UserEntity 생성
        UserEntity sender = userService.getMyUserWithAuthorities();

        // 메시지 발송 로직
        chatMessageService.saveChatMessage(sender, message, roomId);

        // RabbitMQ를 통해 메시지 전송
        rabbitTemplate.convertAndSend("chat.message.exchange", "chat.message.send", roomId);

        return ResponseEntity.ok("Message sent successfully");
    }


    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getLastMessages(@PathVariable Long roomId) {
        if (roomId == null) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "채팅방이 생성되지 않았습니다.");
        }

        List<ChatMessage> chatMessages = chatMessageService.getLastMessages(roomId);

        return ResponseEntity.ok(chatMessages);
    }


}