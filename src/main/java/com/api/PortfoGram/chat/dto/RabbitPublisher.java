package com.api.PortfoGram.chat.dto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.api.PortfoGram.common.RabbitMQConfig;

@Component
@RequiredArgsConstructor
public class RabbitPublisher {

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    public void pubsubMessage(ChatMessage chatMessage) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(chatMessage);
            rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE_NAME, "", jsonMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert chat message to JSON");
        }
    }

    public void publishChatRoomCreateEvent(Long chatRoomId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_ROOM_CREATE_EXCHANGE_NAME, RabbitMQConfig.CHAT_ROOM_CREATE_ROUTING_KEY, chatRoomId);
    }

    public void publishChatRoomJoinEvent(Long chatRoomId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_ROOM_JOIN_EXCHANGE_NAME, RabbitMQConfig.CHAT_ROOM_JOIN_ROUTING_KEY, chatRoomId);
    }
}
