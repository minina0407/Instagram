package com.api.PortfoGram.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import static com.api.PortfoGram.chat.constant.RabbitMQConstant.*;
import static com.api.PortfoGram.chat.service.ChatRoomService.*;

@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String rabbitMqHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitMqPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitMqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitMqPassword;
    //Queue 등록
    @Bean
    public Queue chatQueue() {
        return new Queue(CHAT_QUEUE_NAME, true);
    }


    //Exchange 등록
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    //Exchange - chat Queue 바인딩
    @Bean
    public Binding binding(Queue chatQueue, TopicExchange exchange) {
        return BindingBuilder.bind(chatQueue).to(exchange).with(CHAT_ROUTING_KEY);
    }

    //Exchange 등록 (채팅방 생성)
    @Bean
    public TopicExchange createChatRoomExchange() {
        return new TopicExchange(CHAT_ROOM_CREATE_EXCHANGE_NAME);
    }

    // 바인딩 (채팅방 생성)
    @Bean
    public Binding bindingCreateChatRoom(Queue chatQueue, TopicExchange createChatRoomExchange) {
        return BindingBuilder.bind(chatQueue).to(createChatRoomExchange).with(CHAT_ROOM_CREATE_ROUTING_KEY);
    }

    //Exchange 등록 (채팅방 참가)
    @Bean
    public TopicExchange joinChatRoomExchange() {
        return new TopicExchange(CHAT_ROOM_JOIN_EXCHANGE_NAME);
    }

    // 바인딩 (채팅방 참가)
    @Bean
    public Binding bindingJoinChatRoom(Queue chatQueue, TopicExchange joinChatRoomExchange) {
        return BindingBuilder.bind(chatQueue).to(joinChatRoomExchange).with(CHAT_ROOM_JOIN_ROUTING_KEY);
    }
    // 바이트-자바 Object간 변환
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }


    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(rabbitMqHost);
        factory.setPort(rabbitMqPort);
        factory.setUsername(rabbitMqUsername);
        factory.setPassword(rabbitMqPassword);
        return factory;
    }
}