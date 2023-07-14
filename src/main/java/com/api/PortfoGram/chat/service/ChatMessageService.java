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
    }public List<ChatMessage> getLastMessages(Long roomId) {
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
