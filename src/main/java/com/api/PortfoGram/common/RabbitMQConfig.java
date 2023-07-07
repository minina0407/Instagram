package com.api.PortfoGram.common;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {
    public static final String FANOUT_EXCHANGE_NAME = "pubsub-exchange";
    private static final String QUEUE_NAME_SUB1 = "sub1";
    private static final String QUEUE_NAME_SUB2 = "sub2";

    public static final String CHAT_ROOM_CREATE_EXCHANGE_NAME = "chat_room_create_exchange";
    public static final String CHAT_ROOM_JOIN_EXCHANGE_NAME = "chat_room_join_exchange";
    public static final String CHAT_ROOM_CREATE_ROUTING_KEY = "chat_room_create";
    public static final String CHAT_ROOM_JOIN_ROUTING_KEY = "chat_room_join";

    @Bean
    public Queue subQueue1() {
        return new Queue(QUEUE_NAME_SUB1, false);
    }

    @Bean
    public Queue subQueue2() {
        return new Queue(QUEUE_NAME_SUB2, false);
    }

    @Bean
    public FanoutExchange pubsubExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    @Bean
    public Binding pubsubBinding1(FanoutExchange pubsubExchange, Queue subQueue1) {
        return BindingBuilder.bind(subQueue1).to(pubsubExchange);
    }

    @Bean
    public Binding pubsubBinding2(FanoutExchange pubsubExchange, Queue subQueue2) {
        return BindingBuilder.bind(subQueue2).to(pubsubExchange);
    }

    @Bean
    public FanoutExchange chatRoomCreateExchange() {
        return new FanoutExchange(CHAT_ROOM_CREATE_EXCHANGE_NAME);
    }

    @Bean
    public FanoutExchange chatRoomJoinExchange() {
        return new FanoutExchange(CHAT_ROOM_JOIN_EXCHANGE_NAME);
    }

    @Bean
    public Binding chatRoomCreateBinding(Queue subQueue1, FanoutExchange chatRoomCreateExchange) {
        return BindingBuilder.bind(subQueue1).to(chatRoomCreateExchange);
    }

    @Bean
    public Binding chatRoomJoinBinding(Queue subQueue1, FanoutExchange chatRoomJoinExchange) {
        return BindingBuilder.bind(subQueue1).to(chatRoomJoinExchange);
    }

    @Bean
    public Binding chatRoomCreateBinding2(Queue subQueue2, FanoutExchange chatRoomCreateExchange) {
        return BindingBuilder.bind(subQueue2).to(chatRoomCreateExchange);
    }

    @Bean
    public Binding chatRoomJoinBinding2(Queue subQueue2, FanoutExchange chatRoomJoinExchange) {
        return BindingBuilder.bind(subQueue2).to(chatRoomJoinExchange);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}