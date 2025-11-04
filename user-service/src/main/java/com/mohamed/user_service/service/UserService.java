package com.mohamed.user_service.service;

import com.mohamed.user.*;
import com.mohamed.user_service.service.handler.StockTradeRequestHandler;
import com.mohamed.user_service.service.handler.UserInformationRequestHandler;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserInformationRequestHandler userHandler;
    private final StockTradeRequestHandler stockHandler;

    public UserService(UserInformationRequestHandler userHandler, StockTradeRequestHandler stockHandler) {
        this.userHandler = userHandler;
        this.stockHandler = stockHandler;
    }

    @Override
    public void getUserInformation(UserInformationRequest request, StreamObserver<UserInformation> responseObserver) {
        var userInfo = userHandler.getUserInformation(request);
        responseObserver.onNext(userInfo);
        responseObserver.onCompleted();
    }

    @Override
    public void tradeStock(StockTradeRequest request, StreamObserver<StockTradeResponse> responseObserver) {
        var response = TradeAction.SELL.equals(request.getAction()) ?
                stockHandler.sellStock(request) :
                stockHandler.buyStock(request);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
