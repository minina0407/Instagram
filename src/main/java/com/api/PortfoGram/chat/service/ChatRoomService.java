package com.api.PortfoGram.chat.service;

import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import com.api.PortfoGram.chat.entity.UserChatRoomEntity;
import com.api.PortfoGram.chat.repository.ChatRoomRepository;
import com.api.PortfoGram.chat.repository.UserChatRoomRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    @Transactional
    public Long createNewChatRoom(UserEntity sender, UserEntity receiver) {
        validateParameters(sender, receiver);
        // ChatRoom 생성 및 저장
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .createdAt(LocalDateTime.now())
                .build();
        chatRoomRepository.save(chatRoom);

        // UserChatRoom 생성 및 저장
        UserChatRoomEntity senderUserChatRoom = UserChatRoomEntity.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .build();
        userChatRoomRepository.save(senderUserChatRoom);

        UserChatRoomEntity receiverUserChatRoom = UserChatRoomEntity.builder()
                .chatRoom(chatRoom)
                .user(receiver)
                .build();
        userChatRoomRepository.save(receiverUserChatRoom);

        return chatRoom.getId();
    }

    public void joinChatRoom(Long roomId, UserEntity user) {
        // ChatRoom 조회 및 사용자 추가
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"채팅방을 찾을 수 없습니다"));

        UserChatRoomEntity userChatRoom = UserChatRoomEntity.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        userChatRoomRepository.save(userChatRoom);

    }

    public ChatRoomEntity getChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"채팅방을 찾을 수 없습니다"));
    }

    private void validateParameters(UserEntity sender, UserEntity receiver) {
        if (sender == null || receiver == null) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID);
        }

        Optional<ChatRoomEntity> existingChatRoom =
                chatRoomRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId());
        if (existingChatRoom.isPresent()) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID,"이미 존재하는 채팅방입니다.");
        }
    }
}
