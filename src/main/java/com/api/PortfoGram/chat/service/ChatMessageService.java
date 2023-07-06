package com.api.PortfoGram.chat.service;

import com.api.PortfoGram.chat.dto.ChatMessage;
import com.api.PortfoGram.chat.entity.ChatMessageEntity;
import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import com.api.PortfoGram.chat.repository.ChatMessageRepository;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
    }

    public List<ChatMessage> getLastMessages(Long roomId) {
        ChatRoomEntity chatRoom = chatRoomService.getChatRoomById(roomId);
        List<ChatMessageEntity> chatMessageEntities = chatMessageRepository.findTop20ByChatRoomOrderByCreatedAtDesc(chatRoom);
        return chatMessageEntities.stream()
                .map(ChatMessage::fromEntity)
                .collect(Collectors.toList());
    }
}
