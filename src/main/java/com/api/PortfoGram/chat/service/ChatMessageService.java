package com.api.PortfoGram.chat.service;

import com.api.PortfoGram.chat.dto.ChatMessage;
import com.api.PortfoGram.chat.dto.Message;
import com.api.PortfoGram.chat.entity.ChatMessageEntity;
import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import com.api.PortfoGram.chat.repository.ChatMessageRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.api.PortfoGram.chat.constant.RabbitMQConstant.EXCHANGE_NAME;
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final RabbitTemplate rabbitTemplate;


    @Transactional
    public void saveChatMessage(Long roomId,Message message) {
        ChatRoomEntity chatRoom = chatRoomService.getChatRoomById(roomId);
        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .senderId(message.getSenderId())
                .receiverId(message.getSenderId())
                .content((String) message.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessage);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "chat." + roomId, message);
        log.info("chatRoomId = {}", message.getChannelId());
    }
    public List<ChatMessage> getLastMessages(Long roomId) {
        // DB에서 최근 20개의 채팅 메시지를 가져옵니다.
        List<ChatMessageEntity> chatMessageEntities = chatMessageRepository.findTop20ByChatRoomIdOrderByCreatedAtDesc(roomId);

        // ChatMessageEntity를 ChatMessage로 변환합니다.
        List<ChatMessage> chatMessages = chatMessageEntities.stream()
                .map(ChatMessage::fromEntity)
                .collect(Collectors.toList());

        // 결과를 반환합니다.
        return chatMessages != null ? chatMessages : new ArrayList<>();
    }


}
