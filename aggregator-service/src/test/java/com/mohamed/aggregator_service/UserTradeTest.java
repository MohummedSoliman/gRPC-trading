package com.mohamed.aggregator_service;

import com.mohamed.aggregator_service.mockService.StockMockService;
import com.mohamed.aggregator_service.mockService.UserMockService;
import com.mohamed.common.Ticker;
import com.mohamed.user.StockTradeRequest;
import com.mohamed.user.StockTradeResponse;
import com.mohamed.user.TradeAction;
import com.mohamed.user.UserInformation;
import net.devh.boot.grpc.server.service.GrpcService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest(properties = {
        "grpc.server.port=-1",
        "grpc.server.in-process-name=integration-test",
        "grpc.client.user-service.address=in-process:integration-test",
        "grpc.client.stock-service.address=in-process:integration-test"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTradeTest {

    private static final String USER_INFORMATION_ENDPOINT = "http://localhost:%d/user/%d";
    private static final String TRADE_ENDPOINT = "http://localhost:%d/trade";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void userInformationTest() {
        var url = USER_INFORMATION_ENDPOINT.formatted(port, 1);
        var response = this.restTemplate.getForEntity(url, UserInformation.class);
        Assertions.assertEquals(200, response.getStatusCode().value());
        var user = response.getBody();
        Assertions.assertNotNull(user);
        Assertions.assertEquals(1, user.getUserId());
        Assertions.assertEquals("integration-test", user.getName());
    }

    @Test
    public void unknownUserTest() {
        var url = USER_INFORMATION_ENDPOINT.formatted(port, 2);
        var response = this.restTemplate.getForEntity(url, UserInformation.class);
        Assertions.assertEquals(404, response.getStatusCode().value());
        var user = response.getBody();
        Assertions.assertNull(user);
    }

    @Test
    public void tradeTest() {
        var tradeRequest = StockTradeRequest.newBuilder()
                .setUserId(1)
                .setPrice(10)
                .setTicker(Ticker.APPLE)
                .setAction(TradeAction.BUY)
                .setQuantity(2)
                .build();

        var url = TRADE_ENDPOINT.formatted(port);
        var response = this.restTemplate.postForEntity(url, tradeRequest, StockTradeResponse.class);
        Assertions.assertEquals(200, response.getStatusCode().value());
        var body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(1, body.getUserId());
        Assertions.assertEquals(15, body.getPrice());
        Assertions.assertEquals(Ticker.APPLE, body.getTicker());
    }

    @TestConfiguration
    static class TestConfig {

        @GrpcService
        public StockMockService stockMockService() {
            return new StockMockService();
        }

        @GrpcService
        public UserMockService userMockService() {
            return new UserMockService();
        }
    }
}
