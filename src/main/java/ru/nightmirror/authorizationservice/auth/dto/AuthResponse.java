package ru.nightmirror.authorizationservice.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}
