package com.renova.renova.security;

import com.renova.renova.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * Genera y valida tokens de sesion firmados (HMAC-SHA256) para el panel de
 * administracion. Formato: base64url(payload) + "." + base64url(firma),
 * donde payload = "<usuario>:<expiraEnEpochSegundos>".
 */
@Service
public class TokenService {

    private static final String ALGORITHM = "HmacSHA256";

    private final String secret;
    private final long ttlSeconds;

    public TokenService(
            @Value("${admin.token-secret}") String secret,
            @Value("${admin.token-ttl-minutes}") long ttlMinutes
    ) {
        this.secret = secret;
        this.ttlSeconds = ttlMinutes * 60;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public String generateToken(String username) {
        long expiresAt = Instant.now().getEpochSecond() + ttlSeconds;
        String payload = username + ":" + expiresAt;
        String signature = sign(payload);
        return encode(payload) + "." + signature;
    }

    /** Valida el token y devuelve el nombre de usuario. Lanza UnauthorizedException si no es valido. */
    public String validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Token no proporcionado");
        }
        String[] parts = token.split("\\.", 2);
        if (parts.length != 2) {
            throw new UnauthorizedException("Token invalido");
        }

        String payload = decode(parts[0]);
        String expectedSignature = sign(payload);
        if (!constantTimeEquals(expectedSignature, parts[1])) {
            throw new UnauthorizedException("Token invalido");
        }

        String[] payloadParts = payload.split(":", 2);
        if (payloadParts.length != 2) {
            throw new UnauthorizedException("Token invalido");
        }

        long expiresAt = Long.parseLong(payloadParts[1]);
        if (Instant.now().getEpochSecond() > expiresAt) {
            throw new UnauthorizedException("Token expirado");
        }

        return payloadParts[0];
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        try {
            return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Token invalido");
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        if (aBytes.length != bBytes.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }

    /** Utilidad disponible para generar codigos de seguimiento aleatorios. */
    public static String randomCode(int length) {
        String chars = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
