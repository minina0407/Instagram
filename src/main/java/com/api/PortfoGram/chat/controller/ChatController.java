package com.api.PortfoGram.chat.controller;

import com.api.PortfoGram.chat.dto.ChatMessage;
import com.api.PortfoGram.chat.service.ChatMessageService;
import com.api.PortfoGram.chat.service.ChatRoomService;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "채팅 API", description = "채팅 관련 API")
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final RabbitTemplate rabbitTemplate;
    private final ChatMessageService chatMessageService;
    private final UserService userService;

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
        Long chatRoomId = chatRoomService.createNewChatRoom(sender, receiver);
        rabbitTemplate.convertAndSend("chat.room.exchange", "chat.room.created", chatRoomId);
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
        rabbitTemplate.convertAndSend("chat.room.exchange", "chat.room.join", roomId);
    }

    @PostMapping("/rooms/{roomId}/messages")
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
    public void sendMessage(@Parameter(description = "채팅방 ID", example = "1") @PathVariable("roomId") Long roomId,
                            @Parameter(description = "메시지 내용", example = "test Send Message") @RequestBody String message) {
        if (roomId == null) {
            throw new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND, "채팅방이 존재하지 않습니다.");
        }
        if (StringUtils.isEmpty(message)) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_MISSING, "메시지를 입력해주세요.");
        }
        UserEntity sender = userService.getMyUserWithAuthorities();
        chatMessageService.saveChatMessage(sender, message, roomId);
        rabbitTemplate.convertAndSend("chat.message.exchange", "chat.message.send", roomId);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"채팁앙을 찾을 수 없습니다.\" }")))
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