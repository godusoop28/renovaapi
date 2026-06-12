package com.renova.renova.controller;

import com.renova.renova.dto.LoginRequest;
import com.renova.renova.dto.LoginResponse;
import com.renova.renova.exception.UnauthorizedException;
import com.renova.renova.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final TokenService tokenService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminAuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        if (!adminUsername.equals(request.username()) || !adminPassword.equals(request.password())) {
            throw new UnauthorizedException("Usuario o contraseña incorrectos");
        }
        String token = tokenService.generateToken(adminUsername);
        return new LoginResponse(token, "Bearer", tokenService.getTtlSeconds());
    }
}
