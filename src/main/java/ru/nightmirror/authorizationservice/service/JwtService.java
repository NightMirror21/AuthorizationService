package ru.nightmirror.authorizationservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nightmirror.authorizationservice.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class JwtService {

    @Value("${jwt.secret}")
    String secret;

    @Value("${jwt.ttl-minutes}")
    long ttlMinutes;

    SecretKey key;

    @PostConstruct
    public void initKey() {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generate(User user) {
        Date now = new Date();
        Date exp = Date.from(now.toInstant().plus(Duration.ofMinutes(ttlMinutes)));
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getRoles())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return parse(token).getPayload().getSubject();
    }

    public boolean isValid(String token, User user) {
        Claims claims = parse(token).getPayload();
        return claims.getSubject().equals(user.getUsername()) &&
                claims.getExpiration().after(new Date());
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}
