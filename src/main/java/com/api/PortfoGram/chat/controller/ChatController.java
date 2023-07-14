package com.api.PortfoGram.chat.controller;

import com.api.PortfoGram.chat.dto.ChatMessage;

import com.api.PortfoGram.chat.service.ChatMessageService;
import com.api.PortfoGram.chat.service.ChatRoomService;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.api.PortfoGram.chat.constant.RabbitMQConstant.CHAT_QUEUE_NAME;
import static com.api.PortfoGram.chat.constant.RabbitMQConstant.EXCHANGE_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "채팅 API", description = "채팅 관련 API")
@Slf4j
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;
    @PostMapping("/rooms")
    @Operation(summary = "채팅방 생성", description = "채팅방을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"유효하지 않은 사용자입니다.\" }")))
    })
    public void createChatRoom(@RequestBody ChatMessage chatMessage) {
        UserEntity sender = userService.getMyUserWithAuthorities();
        UserEntity receiver = userService.findById(chatMessage.getReceiverId());
        if (receiver == null) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "유효하지 않은 사용자입니다.");
        }
        chatRoomService.createNewChatRoom(sender, receiver);
    }

    @PostMapping("/rooms/{roomId}/join")
    @Operation(summary = "채팅방 입장", description = "채팅방 입장 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"채팅방을 찾을 수 없습니다.\" }")))

    })
    public void joinChatRoom(@PathVariable Long roomId) {
        UserEntity user = userService.getMyUserWithAuthorities();
        chatRoomService.joinChatRoom(roomId, user);
    }


    @MessageMapping("/rooms/{roomId}/messages")
    @Operation(summary = "메시지 전송", description = "채팅방에 메시지를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"채팅방이 존재하지 않습니다.\" }"))),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"메세지를 입력해주세요.\" }")))

    })
    public void sendMessage(@DestinationVariable("roomId") Long roomId, ChatMessage chatMessage) {
        if (roomId == null) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "채팅방이 존재하지 않습니다.");
        }
        UserEntity sender = userService.getMyUserWithAuthorities();

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "chat." + roomId, chatMessage.toEntity());
        log.info("chatRoomId = {}", roomId);
        chatMessageService.saveChatMessage(sender, chatMessage.getContent(), roomId);
    }

    @RabbitListener(queues = CHAT_QUEUE_NAME) // 메세지가 큐에
    public void receive(ChatMessage chatMessage) {
        log.info("message.get={}", chatMessage.getContent());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"채팅방을 찾을 수 없습니다.\" }")))
    })
    @GetMapping("/rooms/{roomId}/messages")
    @Operation(summary = "채팅방 메시지 조회", description = "해당 채팅방의 최근 메시지를 조회합니다.")
    public List<ChatMessage> getLastMessages(@Parameter(description = "채팅방 ID", example = "1") @PathVariable Long roomId) {
        if (roomId == null) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "채팅방을 찾을 수 없습니다.");
        }
        return chatMessageService.getLastMessages(roomId);
    }

}