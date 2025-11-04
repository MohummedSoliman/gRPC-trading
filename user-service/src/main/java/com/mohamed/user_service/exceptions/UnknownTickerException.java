package com.mohamed.user_service.exceptions;

public class UnknownTickerException extends RuntimeException {

    private static final String MESSAGE = "Ticker with name [%s] is unknown";

    public UnknownTickerException() {
        super(MESSAGE);
    }
}
