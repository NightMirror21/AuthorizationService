package ru.nightmirror.authorizationservice.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.nightmirror.authorizationservice.error.errors.TokenAlreadyRevoked;
import ru.nightmirror.authorizationservice.error.errors.TokenExpired;
import ru.nightmirror.authorizationservice.error.errors.TokenNotFound;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(TokenNotFound.class)
    public ResponseEntity<ApiError> notFound(TokenNotFound ex) {
        return build(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(TokenAlreadyRevoked.class)
    public ResponseEntity<ApiError> conflict(TokenAlreadyRevoked ex) {
        return build(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(TokenExpired.class)
    public ResponseEntity<ApiError> unauthorized(TokenExpired ex) {
        return build(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> badRequest(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, Exception ex) {
        return ResponseEntity.status(status).body(new ApiError(status.value(), ex.getMessage()));
    }

    public record ApiError(int status, String message) {
    }
}
