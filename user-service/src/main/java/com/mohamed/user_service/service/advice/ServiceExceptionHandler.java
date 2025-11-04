package com.mohamed.user_service.service.advice;

import com.mohamed.user_service.exceptions.InsufficientBalanceException;
import com.mohamed.user_service.exceptions.UnKnownUserException;
import com.mohamed.user_service.exceptions.UnknownTickerException;
import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class ServiceExceptionHandler {

    @GrpcExceptionHandler(UnknownTickerException.class)
    public Status handleInvalidArgument(UnknownTickerException e) {
        return Status.INVALID_ARGUMENT.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler(UnKnownUserException.class)
    public Status handleUnknownUser(UnKnownUserException e) {
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler(InsufficientBalanceException.class)
    public Status handlePreconditionFailure(InsufficientBalanceException e) {
        return Status.FAILED_PRECONDITION.withDescription(e.getMessage());
    }
}
