package ru.nightmirror.authorizationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nightmirror.authorizationservice.model.Token;
import ru.nightmirror.authorizationservice.model.User;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllByUserAndRevokedFalseAndExpiredFalse(User user);

    Optional<Token> findByToken(String token);
}
