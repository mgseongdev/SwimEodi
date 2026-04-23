package com.swimeodi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminTokenInterceptor implements HandlerInterceptor {

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if (path.equals("/api/admin/auth")) return true;
        if (method.equals("POST") && path.equals("/api/reports")) return true;
        if (method.equals("GET") && path.startsWith("/api/reports")) return true;

        boolean needsAuth = method.equals("POST") || method.equals("PUT") || method.equals("DELETE")
                || method.equals("PATCH") || path.startsWith("/api/admin/");

        if (needsAuth) {
            String token = request.getHeader("X-Admin-Token");
            if (!adminPassword.equals(token)) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"인증이 필요합니다\"}");
                return false;
            }
        }
        return true;
    }
}
