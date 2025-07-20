package ru.nightmirror.authorizationservice.error.errors;

public class TokenException extends RuntimeException {
  public TokenException(String message) {
    super(message);
  }
}
