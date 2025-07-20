package ru.nightmirror.authorizationservice.error.errors;

public class TokenExpired extends TokenException {
    public TokenExpired() {
        super("Token expired");
    }
}
