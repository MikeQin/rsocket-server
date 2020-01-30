package com.example.rsocket.server;

import com.example.rsocket.model.MarketData;
import com.example.rsocket.model.MarketDataRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ContextConfiguration(classes = {RSocketServerApp.class, ClientTestConfiguration.class})
@TestPropertySource(properties = {"spring.rsocket.server.port=7000"})
public class MarketDataRSocketControllerTest {

    @Autowired
    private RSocketRequester rSocketRequester;

    @SpyBean
    private MarketDataRSocketController rSocketController;

    @Test
    public void whenGetsFireAndForget_ThenReturnsNoResponse() throws InterruptedException {
        final MarketData marketData = new MarketData("X", 1);
        rSocketRequester.route("collectMarketData")
                .data(marketData)
                .send()
                .block();

        sleepForProcessing();

        rSocketController.collectMarketData(marketData);

        verify(rSocketController).collectMarketData(any());
    }

    @Test
    public void whenGetsRequest_ThenReturnsResponse() throws InterruptedException {
        final MarketDataRequest marketDataRequest = new MarketDataRequest("X");
        rSocketRequester.route("currentMarketData")
                .data(marketDataRequest)
                .send()
                .block(Duration.ofSeconds(10));

        sleepForProcessing();

        rSocketController.currentMarketData(marketDataRequest);

        verify(rSocketController).currentMarketData(any(MarketDataRequest.class));
    }

    @Test
    public void whenGetsRequest_ThenReturnsStream() throws InterruptedException {
        final MarketDataRequest marketDataRequest = new MarketDataRequest("X");
        rSocketRequester.route("feedMarketData")
                .data(marketDataRequest)
                .send()
                .block(Duration.ofSeconds(10));

        sleepForProcessing();

        rSocketController.feedMarketData(marketDataRequest);

        verify(rSocketController).feedMarketData(any(MarketDataRequest.class));
    }

    private void sleepForProcessing() throws InterruptedException {
        Thread.sleep(1000);
    }

}