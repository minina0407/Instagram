package common;

import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.UUID;

public class MdcLoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String handlerName = handlerMethod.getMethod().getName();
        String methodName = handlerMethod.getBeanType().getSimpleName();
        String controllerInfo = methodName  + "." + handlerName;
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        MDC.put("serviceName", controllerInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        MDC.clear();
    }
}

