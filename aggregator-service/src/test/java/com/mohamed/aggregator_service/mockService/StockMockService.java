package com.mohamed.aggregator_service.mockService;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Empty;
import com.mohamed.common.Ticker;
import com.mohamed.stock.PriceUpdate;
import com.mohamed.stock.StockPriceRequest;
import com.mohamed.stock.StockPriceResponse;
import com.mohamed.stock.StockServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class StockMockService extends StockServiceGrpc.StockServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(StockMockService.class);

    @Override
    public void getStockPrice(StockPriceRequest request, StreamObserver<StockPriceResponse> responseObserver) {
        var response = StockPriceResponse.newBuilder()
                .setPrice(15)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getPriceUpdates(Empty request, StreamObserver<PriceUpdate> responseObserver) {
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        for (int i = 0; i < 5; i++) {
            var priceUpdate = PriceUpdate.newBuilder()
                    .setPrice(i)
                    .setTicker(Ticker.APPLE)
                    .build();
            responseObserver.onNext(priceUpdate);
        }
        responseObserver.onCompleted();
    }
}
