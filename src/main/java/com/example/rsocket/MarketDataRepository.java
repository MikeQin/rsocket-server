package com.example.rsocket;

import com.example.rsocket.model.MarketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
@Repository
public class MarketDataRepository {

    private static final int BOUND = 100;

    private Random random = new Random();

    public Flux<MarketData> getAll(String stock) {
        return Flux.fromStream(Stream.generate(() -> getMarketDataResponse(stock)))
                .log()
                .delayElements(Duration.ofSeconds(1));
    }

    public Mono<MarketData> getOne(String stock) {

        return Mono.just(getMarketDataResponse(stock));
    }

    public void add(MarketData marketData) {

        log.info("Market data: {}", marketData);
    }

    private MarketData getMarketDataResponse(String stock) {

        return new MarketData(stock, random.nextInt(BOUND));
    }
}