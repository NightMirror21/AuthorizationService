package ru.nightmirror.authorizationservice.auth;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.nightmirror.authorizationservice.auth.dto.AuthRequest;
import ru.nightmirror.authorizationservice.auth.dto.AuthResponse;
import ru.nightmirror.authorizationservice.auth.dto.RegisterRequest;
import ru.nightmirror.authorizationservice.model.Role;
import ru.nightmirror.authorizationservice.model.User;

import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    private static final String BEARER = "Bearer ";

    AuthenticationService auth;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        return auth.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid AuthRequest request) {
        return auth.authenticate(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        return auth.refresh(extractToken(header));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        auth.logout(extractToken(header));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public Set<Role> roles(Authentication authn) {
        return ((User) authn.getPrincipal()).getRoles();
    }

    private static String extractToken(String header) {
        if (header == null || !header.startsWith(BEARER)) {
            throw new IllegalArgumentException("Missing Bearer header");
        }
        return header.substring(BEARER.length());
    }
}
