package ru.nightmirror.authorizationservice.error.errors;

public class TokenAlreadyRevoked extends TokenException {
    public TokenAlreadyRevoked() {
        super("Token already revoked");
    }
}
