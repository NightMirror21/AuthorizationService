package ru.nightmirror.authorizationservice.error.errors;

public class TokenNotFound extends TokenException {
    public TokenNotFound() {
        super("Token not found");
    }
}
