package com.pstreaming;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@EnableScheduling
public class RateLimitingFilter implements Filter {
    private static final int MAX_REQUESTS_PER_MINUTE = 3;
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        String clientIP = httpRequest.getRemoteAddr();
        requestCounts.putIfAbsent(clientIP, new AtomicInteger(0));

        int currentRequest = requestCounts.get(clientIP).incrementAndGet();

        if (currentRequest > MAX_REQUESTS_PER_MINUTE) {
            httpResponse.setStatus(429);
            return;
        }
        chain.doFilter(request, response);
    }

    @Scheduled(fixedRate = 60000)
    public void resetRequestCounts() {
        requestCounts.clear();
    }
}
