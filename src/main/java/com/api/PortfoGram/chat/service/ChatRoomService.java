package com.api.PortfoGram.chat.service;

import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import com.api.PortfoGram.chat.entity.UserChatRoomEntity;
import com.api.PortfoGram.chat.repository.ChatRoomRepository;
import com.api.PortfoGram.chat.repository.UserChatRoomRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;
    public static final String CHAT_ROOM_CREATE_EXCHANGE_NAME = "chat_room_create_exchange";
    public static final String CHAT_ROOM_JOIN_EXCHANGE_NAME = "chat_room_join_exchange";
    public static final String CHAT_ROOM_CREATE_ROUTING_KEY = "chat_room_create";
    public static final String CHAT_ROOM_JOIN_ROUTING_KEY = "chat_room_join";
    @Transactional
    public Long createNewChatRoom(Long senderId,Long receiverId) {
        validateParameters(senderId,receiverId);

        ChatRoomEntity chatRoom = createAndSaveChatRoom(senderId, receiverId);

        saveUserChatRoom(senderId, chatRoom);
        saveUserChatRoom(receiverId, chatRoom);

        return chatRoom.getId();
    }

    private ChatRoomEntity createAndSaveChatRoom(Long senderId, Long receiverId){
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .createdAt(LocalDateTime.now())
                .build();
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    private void saveUserChatRoom(Long userId, ChatRoomEntity chatRoom){
        UserEntity user = userService.findById(userId);
        UserChatRoomEntity userChatRoom = UserChatRoomEntity.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        userChatRoomRepository.save(userChatRoom);
    }
    public void joinChatRoom(Long roomId, Long userId) {
        // ChatRoom 조회 및 사용자 추가
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"채팅방을 찾을 수 없습니다"));

        // 이미 채팅방에 참여한 사용자인지 확인
        if (chatRoom.getUsers().contains(userId)) {
            throw new BadRequestException("이미 채팅방에 참여한 사용자입니다.");
        }
        //채팅방에 새로운 사용자 추가
        UserEntity curUser = userService.findById(userId);

        UserChatRoomEntity userChatRoom = UserChatRoomEntity.builder()
                .chatRoom(chatRoom)
                .user(curUser)
                .build();
        userChatRoomRepository.save(userChatRoom);

        rabbitTemplate.convertAndSend(CHAT_ROOM_JOIN_EXCHANGE_NAME, CHAT_ROOM_JOIN_ROUTING_KEY, roomId);


    }

    public ChatRoomEntity getChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"채팅방을 찾을 수 없습니다"));
    }

    private void validateParameters(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID);
        }

        Optional<ChatRoomEntity> existingChatRoom =
                chatRoomRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        if (existingChatRoom.isPresent()) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID,"이미 존재하는 채팅방입니다.");
        }
    }


}
