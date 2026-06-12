package com.renova.renova.security;

import com.renova.renova.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Protege las rutas /api/admin/** exigiendo un token Bearer valido. */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AdminAuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        if (path.endsWith("/api/admin/auth/login")) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("Se requiere autenticacion de administrador");
        }

        String token = header.substring("Bearer ".length());
        tokenService.validateToken(token);
        return true;
    }
}
