package com.example.rsocket.server;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.time.Duration;

@TestConfiguration
public class ClientTestConfiguration {

    @Bean
    @Lazy
    public RSocketRequester rSocketRequester(RSocketRequester.Builder rSocketRequesterBuilder) {
        return rSocketRequesterBuilder.connectTcp("localhost", 7000)
                .block(Duration.ofSeconds(5));
    }


    /**
     * Alternative way
     *
    @Bean
    @Lazy
    public RSocket rSocket() {

        return RSocketFactory.connect()
                .mimeType(MimeTypeUtils.APPLICATION_JSON_VALUE, MimeTypeUtils.APPLICATION_JSON_VALUE)
                .frameDecoder(PayloadDecoder.ZERO_COPY)
                .transport(TcpClientTransport.create(7000))
                .start()
                .block();
    }

    @Bean
    @Lazy
    RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies) {
        return RSocketRequester.wrap(rSocket(), MimeTypeUtils.APPLICATION_JSON,
                MimeTypeUtils.APPLICATION_JSON, rSocketStrategies);
    }*/
}
