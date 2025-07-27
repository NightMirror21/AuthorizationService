package ru.nightmirror.authorizationservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.nightmirror.authorizationservice.hash.TokenHash;
import ru.nightmirror.authorizationservice.model.Token;
import ru.nightmirror.authorizationservice.model.User;
import ru.nightmirror.authorizationservice.repository.TokenRepository;
import ru.nightmirror.authorizationservice.repository.UserRepository;
import ru.nightmirror.authorizationservice.service.JwtService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class JwtAuthFilter extends OncePerRequestFilter {

    JwtService jwt;
    UserRepository users;
    TokenRepository tokens;
    ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        try {
            String header = req.getHeader(HttpHeaders.AUTHORIZATION);
            if (header == null || !header.startsWith("Bearer ")) {
                chain.doFilter(req, res);
                return;
            }

            String token = header.substring(7);
            if (token.isBlank() || !isJwtLike(token)) {
                chain.doFilter(req, res);
                return;
            }

            String username = jwt.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String hash = TokenHash.sha256Hex(token);
                User user = users.findByUsername(username).orElse(null);
                boolean validInDb = tokens.findByTokenHashAndType(hash, Token.TokenType.ACCESS)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);

                if (user != null && validInDb && jwt.isValid(token, user)) {
                    var roles = user.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                            .toList();

                    SecurityContextHolder.getContext()
                            .setAuthentication(new UsernamePasswordAuthenticationToken(user, null, roles));
                }
            }

            chain.doFilter(req, res);
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            sendJsonError(res, e);
        }

    }

    private void sendJsonError(HttpServletResponse res, JwtException exception) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Invalid or expired JWT");
        body.put("message", exception.getMessage());
        objectMapper.writeValue(res.getWriter(), body);
    }

    private static boolean isJwtLike(String t) {
        return t.chars().filter(ch -> ch == '.').count() == 4;
    }

}
