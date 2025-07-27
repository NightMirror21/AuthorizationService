package ru.nightmirror.authorizationservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nightmirror.authorizationservice.model.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class JwtService {

    @Getter
    @Value("${jwt.ttl-minutes}")
    long ttlMinutes;

    @Value("${jwt.signing.secret}")
    String signingSecretB64;

    @Value("${jwt.encryption.secret}")
    String encryptionSecretB64;

    private static final String ISSUER = "auth-service";
    private static final String AUDIENCE = "api";

    SecretKey signKey;
    SecretKey encKey;

    @PostConstruct
    public void initKey() {
        byte[] signBytes = Decoders.BASE64.decode(signingSecretB64);
        byte[] encBytes  = Decoders.BASE64.decode(encryptionSecretB64);

        signKey = Keys.hmacShaKeyFor(signBytes);
        encKey  = new SecretKeySpec(encBytes, "AES");
    }

    public String generate(User user) {
        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plus(Duration.ofMinutes(ttlMinutes)));

        String jws = Jwts.builder()
                .header().type("at+jwt").and()
                .issuer(ISSUER)
                .audience().add(AUDIENCE).and()
                .subject(user.getUsername())
                .id(UUID.randomUUID().toString())
                .issuedAt(iat)
                .expiration(exp)
                .signWith(signKey, Jwts.SIG.HS256)
                .compact();

        return Jwts.builder()
                .content(jws, "JWT")
                .encryptWith(encKey, Jwts.ENC.A256GCM)
                .compact();
    }

    public String extractUsername(String outerJwe) {
        return parseClaims(outerJwe).getSubject();
    }

    public Claims parseClaims(String outerJwe) {
        String jws = new String(
                Jwts.parser()
                        .decryptWith(encKey)
                        .build()
                        .parseEncryptedContent(outerJwe)
                        .getPayload(),
                StandardCharsets.UTF_8
        );

        return Jwts.parser()
                .verifyWith(signKey)
                .requireIssuer(ISSUER)
                .requireAudience(AUDIENCE)
                .build()
                .parseSignedClaims(jws)
                .getPayload();
    }

    public boolean isValid(String outerJwe, User user) {
        Claims c = parseClaims(outerJwe);
        return user.getUsername().equals(c.getSubject())
                && c.getExpiration().after(new Date());
    }
}
