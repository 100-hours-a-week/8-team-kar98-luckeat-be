package com.luckeat.luckeatbackend.common.interceptor;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * API 요청 시간을 측정하는 인터셉터
 */
@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private final MeterRegistry meterRegistry;
    private final String REQUEST_START_TIME = "requestStartTime";

    @Autowired
    public MetricsInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(REQUEST_START_TIME, System.nanoTime());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // do nothing
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            
            String uri = request.getRequestURI();
            String method = request.getMethod();
            long startTime = (long) request.getAttribute(REQUEST_START_TIME);
            long duration = System.nanoTime() - startTime;
            
            // API 요청 처리 시간 측정
            Timer timer = meterRegistry.timer("luckeat.api.request.duration",
                    "uri", uri,
                    "method", method,
                    "status", String.valueOf(response.getStatus()),
                    "handler", handlerMethod.getBeanType().getSimpleName() + "." + handlerMethod.getMethod().getName());
            
            timer.record(duration, TimeUnit.NANOSECONDS);
        }
    }
} 