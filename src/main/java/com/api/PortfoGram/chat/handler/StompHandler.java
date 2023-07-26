package com.api.PortfoGram.chat.handler;

import com.api.PortfoGram.auth.utils.JwtTokenProvider;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행됨
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // websocket 연결시 헤더의 jwt token 유효성 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new BadCredentialsException("Invalid JWT token");
            }

            String token = authorizationHeader.substring(7); // "Bearer " 이후의 토큰 부분 추출

            try {
                // 토큰 유효성 검증
                if (!jwtTokenProvider.validateAccessToken(token)) {
                    throw new BadCredentialsException("Invalid JWT token");
                }
                // 토큰에서 사용자 ID 추출
                Long userId = userService.getUserIdFromToken(token);
                // 추출한 사용자 ID를 헤더에 추가하여 메시지 전달
                accessor.setUser(new UsernamePasswordAuthenticationToken(userId, token, new ArrayList<>()));
            } catch (BadCredentialsException e) {
                // 클라이언트에게 에러 메시지 전송
                throw new MessageDeliveryException("Invalid JWT token");
            }
        }

        return message;
    }
}
