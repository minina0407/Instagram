package com.api.PortfoGram.chat.repository;

import com.api.PortfoGram.chat.entity.UserChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoomEntity, Long> {
}
