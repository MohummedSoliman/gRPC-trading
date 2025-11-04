package com.mohamed.user_service.exceptions;

public class InsufficientBalanceException extends RuntimeException {

    private static final String MESSAGE = "User does not have enough fund to complete transaction";

    public InsufficientBalanceException() {
        super(MESSAGE);
    }
}
