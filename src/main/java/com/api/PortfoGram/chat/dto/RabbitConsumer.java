package com.api.PortfoGram.chat.dto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RabbitConsumer {

    @RabbitListener(queues = "#{subQueue1.name}")
    public void consumeSub1(ChatMessage message) {
        log.info("[consumeSub1]: {}", message);
    }

    @RabbitListener(queues = "#{subQueue2.name}")
    public void consumeSub2(ChatMessage message) {
        log.info("[consumeSub2]: {}", message);
    }
}
