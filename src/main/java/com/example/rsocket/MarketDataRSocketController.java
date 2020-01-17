package com.example.rsocket;

import com.example.rsocket.model.MarketData;
import com.example.rsocket.model.MarketDataRequest;
import com.example.rsocket.model.MarketDataError;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MarketDataRSocketController {

    private final MarketDataRepository marketDataRepository;

    public MarketDataRSocketController(MarketDataRepository marketDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }

    @MessageMapping("currentMarketData")
    public Mono<MarketData> currentMarketData(MarketDataRequest marketDataRequest) {
        return marketDataRepository.getOne(marketDataRequest.getStock());
    }

    @MessageMapping("feedMarketData")
    public Flux<MarketData> feedMarketData(MarketDataRequest marketDataRequest) {
        return marketDataRepository.getAll(marketDataRequest.getStock());
    }

    @MessageMapping("collectMarketData")
    public Mono<Void> collectMarketData(MarketData marketData) {
        marketDataRepository.add(marketData);
        return Mono.empty();
    }

    @MessageExceptionHandler
    public Mono<MarketDataError> handleException(Exception e) {

        return Mono.just(new MarketDataError(e));
    }
}