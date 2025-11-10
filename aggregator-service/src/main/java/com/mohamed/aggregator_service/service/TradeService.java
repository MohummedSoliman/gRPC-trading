package com.mohamed.aggregator_service.service;

import com.mohamed.stock.StockPriceRequest;
import com.mohamed.stock.StockServiceGrpc;
import com.mohamed.user.StockTradeRequest;
import com.mohamed.user.StockTradeResponse;
import com.mohamed.user.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userClient;

    @GrpcClient("stock-service")
    private StockServiceGrpc.StockServiceBlockingStub stockClient;

    public StockTradeResponse trade(StockTradeRequest request) {
        var priceRequest = StockPriceRequest.newBuilder()
                .setTicker(request.getTicker())
                .build();

        var priceResponse = this.stockClient.getStockPrice(priceRequest);
        var tradeRequest = request.toBuilder()
                .setPrice(priceResponse.getPrice())
                .build();

        return this.userClient.tradeStock(tradeRequest);
    }
}
