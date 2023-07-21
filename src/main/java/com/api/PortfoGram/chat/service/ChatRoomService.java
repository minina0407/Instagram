package com.api.PortfoGram.chat.service;

import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import com.api.PortfoGram.chat.entity.UserChatRoomEntity;
import com.api.PortfoGram.chat.repository.ChatRoomRepository;
import com.api.PortfoGram.chat.repository.UserChatRoomRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final RabbitTemplate rabbitTemplate;
    public static final String CHAT_ROOM_CREATE_EXCHANGE_NAME = "chat_room_create_exchange";
    public static final String CHAT_ROOM_JOIN_EXCHANGE_NAME = "chat_room_join_exchange";
    public static final String CHAT_ROOM_CREATE_ROUTING_KEY = "chat_room_create";
    public static final String CHAT_ROOM_JOIN_ROUTING_KEY = "chat_room_join";
    @Transactional
    public Long createNewChatRoom(UserEntity sender, UserEntity receiver) {
        validateParameters(sender, receiver);

        ChatRoomEntity chatRoom = createAndSaveChatRoom(sender, receiver);

        saveUserChatRoom(sender, chatRoom);
        saveUserChatRoom(receiver, chatRoom);

        rabbitTemplate.convertAndSend(CHAT_ROOM_CREATE_EXCHANGE_NAME, CHAT_ROOM_CREATE_ROUTING_KEY, chatRoom.getId());

        return chatRoom.getId();
    }

    private ChatRoomEntity createAndSaveChatRoom(UserEntity sender, UserEntity receiver){
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .createdAt(LocalDateTime.now())
                .build();
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    private void saveUserChatRoom(UserEntity user, ChatRoomEntity chatRoom){
        UserChatRoomEntity userChatRoom = UserChatRoomEntity.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        userChatRoomRepository.save(userChatRoom);
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

        rabbitTemplate.convertAndSend(CHAT_ROOM_JOIN_EXCHANGE_NAME, CHAT_ROOM_JOIN_ROUTING_KEY, roomId);

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
