package com.mohamed.user_service;

import com.mohamed.common.Ticker;
import com.mohamed.user.StockTradeRequest;
import com.mohamed.user.UserInformationRequest;
import com.mohamed.user.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "grpc.server.port=-1",
        "grpc.server.in-process-name=integration-test",
        "grpc.client.user-service.address=in-process:integration-test"
})
public class UserServiceTest {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub stub;

    @Test
    public void userInformationTest() {
        var request = UserInformationRequest.newBuilder()
                .setUserId(1)
                .build();

        var response = this.stub.getUserInformation(request);

        Assertions.assertEquals(10_000, response.getBalance());
        Assertions.assertEquals("Sam", response.getName());
    }

    @Test
    public void unknownUserTest() {
        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var request = UserInformationRequest.newBuilder()
                    .setUserId(10)
                    .build();

            var response = this.stub.getUserInformation(request);
        });

        Assertions.assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
    }

    @Test
    public void unknownTickerTest() {
        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var request = StockTradeRequest.newBuilder()
                    .setUserId(1)
                    .setTicker(Ticker.UNKNOWN)
                    .build();

            var response = this.stub.tradeStock(request);
        });

        Assertions.assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
    }

    @Test
    public void insufficientBalanceTest() {
        var ex = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            var request = StockTradeRequest.newBuilder()
                    .setUserId(1)
                    .setTicker(Ticker.APPLE)
                    .setQuantity(1000)
                    .setPrice(1000)
                    .build();

            var response = this.stub.tradeStock(request);
        });

        Assertions.assertEquals(Status.FAILED_PRECONDITION.getCode(), ex.getStatus().getCode());
    }
}
