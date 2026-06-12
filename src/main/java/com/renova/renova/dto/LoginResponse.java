package com.renova.renova.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds
) {
}
