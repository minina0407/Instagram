package com.api.PortfoGram.chat.repository;

import com.api.PortfoGram.chat.entity.ChatMessageEntity;
import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findTop20ByChatRoomOrderByCreatedAtDesc(ChatRoomEntity chatRoom);
}
