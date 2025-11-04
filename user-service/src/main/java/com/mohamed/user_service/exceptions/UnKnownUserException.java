package com.mohamed.user_service.exceptions;

public class UnKnownUserException extends RuntimeException {

    private static final String MESSAGE = "User [id=%d] Not Found";

    public UnKnownUserException(Integer userId) {
        super(MESSAGE.formatted(userId));
    }
}
