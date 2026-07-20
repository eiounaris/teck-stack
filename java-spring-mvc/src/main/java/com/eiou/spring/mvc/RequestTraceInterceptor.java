package com.eiou.spring.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class RequestTraceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        response.setHeader("X-Spring-Mvc-Interceptor", "RequestTraceInterceptor");
        System.out.println("[mvc-interceptor] " + request.getMethod() + " " + request.getRequestURI());
        return true;
    }
}
