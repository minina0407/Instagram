package common;

import com.api.PortfoGram.auth.utils.JwtTokenProvider;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    // WebSocket 인터셉터를 등록하는 메서드를 추가합니다.
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                StompCommand command = accessor.getCommand();

                if (StompCommand.SUBSCRIBE.equals(command) || StompCommand.SEND.equals(command)) {
                    // 토큰을 가져와서 검증한 다음 사용자를 가져오고 Principal 객체에 설정합니다.
                    String jwtToken = accessor.getFirstNativeHeader("Authorization");
                    if (jwtToken != null && jwtTokenProvider.validateAccessToken(jwtToken)) {
                        String username = jwtTokenProvider.getAuthentication(jwtToken).getName();
                        accessor.setUser(() -> username);
                    } else {
                        throw new BadRequestException(ExceptionEnum.RESPONSE_TOKEN_INVALID,"Invalid Jwt token");
                    }
                }
                return message;
            }
        });
    }
}
