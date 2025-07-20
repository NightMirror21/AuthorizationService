package ru.nightmirror.authorizationservice.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.nightmirror.authorizationservice.model.Role;

import java.util.Set;

public record RegisterRequest(
        @NotBlank(message = "Логин обязателен")
        @Size(min = 3, max = 32, message = "Длина логина 3‑32 символа")
        String username,

        @NotBlank(message = "E‑mail обязателен")
        @Email(message = "Некорректный e‑mail")
        String email,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, max = 128, message = "Пароль 6‑128 символов")
        String password,

        Set<Role> roles
) {}
