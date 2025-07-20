package ru.nightmirror.authorizationservice.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nightmirror.authorizationservice.auth.dto.AuthRequest;
import ru.nightmirror.authorizationservice.auth.dto.AuthResponse;
import ru.nightmirror.authorizationservice.auth.dto.RegisterRequest;
import ru.nightmirror.authorizationservice.error.errors.TokenAlreadyRevoked;
import ru.nightmirror.authorizationservice.error.errors.TokenExpired;
import ru.nightmirror.authorizationservice.error.errors.TokenNotFound;
import ru.nightmirror.authorizationservice.model.Role;
import ru.nightmirror.authorizationservice.model.Token;
import ru.nightmirror.authorizationservice.model.Token.TokenType;
import ru.nightmirror.authorizationservice.model.User;
import ru.nightmirror.authorizationservice.repository.TokenRepository;
import ru.nightmirror.authorizationservice.repository.UserRepository;
import ru.nightmirror.authorizationservice.service.JwtService;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository users;
    TokenRepository tokens;
    PasswordEncoder encoder;
    JwtService jwt;
    PasswordEncoder password;

    @Transactional
    public AuthResponse register(RegisterRequest rq) {
        if (users.existsByUsername(rq.username()) || users.existsByEmail(rq.email()))
            throw new IllegalArgumentException("username or e‑mail already taken");

        Set<Role> roles = rq.roles() == null || rq.roles().isEmpty()
                ? Set.of(Role.GUEST)
                : rq.roles();

        User user = users.save(User.builder()
                .username(rq.username())
                .email(rq.email())
                .password(password.encode(rq.password()))
                .roles(roles)
                .build());

        return issueTokens(user);
    }


    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        User user = users.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!encoder.matches(request.password(), user.getPassword()))
            throw new BadCredentialsException("Bad credentials");
        revokeActiveTokens(user);
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        Token stored = tokens.findByToken(refreshToken)
                .orElseThrow(TokenNotFound::new);

        if (stored.isRevoked()) throw new TokenAlreadyRevoked();
        if (stored.isExpired()) throw new TokenExpired();

        stored.setRevoked(true);
        tokens.save(stored);

        return issueTokens(stored.getUser());
    }

    @Transactional
    public void logout(String accessToken) {
        Token token = tokens.findByToken(accessToken)
                .orElseThrow(TokenNotFound::new);

        if (token.isRevoked()) throw new TokenAlreadyRevoked();

        token.setRevoked(true);
    }

    private void verifyUniqueness(RegisterRequest request) {
        if (users.existsByUsername(request.username()) || users.existsByEmail(request.email()))
            throw new IllegalArgumentException("Username or e‑mail already used");
    }

    private AuthResponse issueTokens(User user) {
        Instant now = Instant.now();
        String access = jwt.generate(user);
        String refresh = UUID.randomUUID().toString();

        tokens.save(Token.builder()
                .token(access)
                .type(TokenType.ACCESS)
                .expiresAt(now.plus(Duration.ofMinutes(15)))
                .user(user)
                .build());

        tokens.save(Token.builder()
                .token(refresh)
                .type(TokenType.REFRESH)
                .expiresAt(now.plus(Duration.ofDays(7)))
                .user(user)
                .build());

        return new AuthResponse(access, refresh);
    }

    private void revokeActiveTokens(User user) {
        tokens.findAllByUserAndRevokedFalseAndExpiredFalse(user)
                .forEach(t -> {
                    t.setRevoked(true);
                    t.setExpired(true);
                });
    }
}
