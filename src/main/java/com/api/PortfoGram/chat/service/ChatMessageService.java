package com.api.PortfoGram.chat.service;

import com.api.PortfoGram.chat.dto.ChatMessage;
import com.api.PortfoGram.chat.entity.ChatMessageEntity;
import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import com.api.PortfoGram.chat.repository.ChatMessageRepository;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final RedisTemplate redisTemplate;

    @Transactional
    public void saveChatMessage(UserEntity sender, String content, Long receiverId) {
        ChatRoomEntity chatRoom = chatRoomService.getChatRoomById(receiverId);
        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .senderId(sender.getId())
                .receiverId(receiverId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessage);
    }
    public void saveChatMessageToRedis(ChatMessage chatMessage) {
        // Redis에 메시지 저장
        String redisKey = "chatMessages:" + chatMessage.getChatRoomId();
        redisTemplate.opsForList().leftPush(redisKey, chatMessage.toEntity());
    }
    public List<ChatMessage> getLastMessages(Long roomId) {
        String redisKey = "chatMessages:" + roomId;
        List<ChatMessage> chatMessages = redisTemplate.opsForList().range(redisKey, 0, 19);

        if (chatMessages != null && !chatMessages.isEmpty()) {
            return chatMessages;
        } else {
            syncChatMessagesToDB(roomId);
            chatMessages = redisTemplate.opsForList().range(redisKey, 0, 19);

            return chatMessages != null ? chatMessages : new ArrayList<>();
        }
    }
    @Scheduled(cron = "0/60 * * * * *") // 분 단위로 실행
    public void syncChatMessagesToDB(Long roomId) {
        String redisKey = "chatMessages:" + roomId;
        List<ChatMessage> chatMessages = redisTemplate.opsForList().range(redisKey, 0, 19);

        if (!chatMessages.isEmpty()) {
            List<ChatMessageEntity> newChatMessages = chatMessages.stream()
                    .map(ChatMessage::toEntity)
                    .collect(Collectors.toList());

            chatMessageRepository.saveAll(newChatMessages);
            redisTemplate.opsForList().trim(redisKey, 0, 19);
        }
    }

}
