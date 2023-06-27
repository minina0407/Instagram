package com.api.PortfoGram.chat.repository;

import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    Optional<ChatRoomEntity>findBySenderIdAndReceiverId(@Param("senderId")Long senderId, @Param("receiverId")Long receiverId);

}
