# Spring RSocket Server

### What is RSocket?
RSocket is a bi-directional message-driven communication protocol. It features an advanced communication flow beyond standard request/response. You can response with a single message, a stream of messages or don’t response at all. A server can also start a communication on an established channel with a client.

RSocket allows you to communicate using following transport protocols:

- TCP
- WebSocket
- Argon
- HTTP/2 Stream

RSocket is a binary protocol for use on byte stream transports. It enables symmetric interaction models via async message passing over a single connection.

The spring-messaging module of the Spring Framework provides support for RSocket requesters and responders, both on the client and on the server side. See the RSocket section of the Spring Framework reference for more details, including an overview of the RSocket protocol.

The spring-messaging module contains the following:

### Spring Support

* RSocketRequester — fluent API to make requests through an io.rsocket.RSocket with data and metadata encoding/decoding.

* Annotated Responders — @MessageMapping annotated handler methods for responding.

### Server Controller
```java
@Controller
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
    public Mono<MarketData> handleException(Exception e) {
        return Mono.just(MarketData.fromException(e));
    }
}
```

### Server Configuration

```yaml
server:
    port: 8081
```

No Java code changes. Only changes in `application.yml`

#### RSocket TCP Server Configuration
```yaml
spring:
    rsocket:
        server:
            port: 7000
            transport: tcp
```

#### RSocket WebSocket Server Configuration
```yaml
spring:
    rsocket:
        server:
            transport: websocket
            mapping-path: /rsocket
```

### Client Configuration
```java
@Configuration
public class RSocketClientConfig {

    @Value("${spring.rsocket.server.address}")
    private String host;
    @Value("${spring.rsocket.server.port}")
    private int port;

    //********************************************
    // Actual connect and use the requester
    //********************************************

    @Qualifier("tcpClient")
    @Bean
    public RSocketRequester tcpClientRequester(RSocketRequester.Builder rSocketRequesterBuilder) {
        return rSocketRequesterBuilder.connectTcp(host, port)
                .block(Duration.ofSeconds(5));
    }

    @Qualifier("webSocketClient")
    @Bean
    public RSocketRequester webSocketClientRequester(RSocketRequester.Builder rSocketRequesterBuilder,
                                                     RSocketStrategies strategies) {
        return RSocketRequester.builder().rsocketStrategies(strategies)
                .connectWebSocket(URI.create("ws://localhost:8081/rsocket"))
                .block(Duration.ofSeconds(5));
    }

    //********************************************
    // Deferred to connect and use the requester
    //********************************************

    @Qualifier("monoTCPClient")
    @Bean
    public Mono<RSocketRequester> monoTCPClientRequester(RSocketRequester.Builder rSocketRequesterBuilder) {
        return rSocketRequesterBuilder.connectTcp(host, port);
    }

    @Qualifier("monoWebSocketClient")
    @Bean
    public Mono<RSocketRequester> monoWebSocketClientRequester(RSocketRequester.Builder rSocketRequesterBuilder,
                                                               RSocketStrategies strategies) {
        return RSocketRequester.builder().rsocketStrategies(strategies)
                .connectWebSocket(URI.create("ws://localhost:8081/rsocket"));
    }
}
```

#### Client Server Port

```yaml
server:
    port: 8080
```

### Client RestController

```java
@RestController
@Slf4j
public class MarketDataRestController {

    private final static Random random = new Random();
    private RSocketRequester rSocketRequester;

    public MarketDataRestController(@Qualifier("webSocketClient") RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @GetMapping(value = "/current/{stock}")
    public Publisher<MarketData> current(@PathVariable("stock") String stock) {
        return rSocketRequester.route("currentMarketData")
                .data(new MarketDataRequest(stock))
                .retrieveMono(MarketData.class);
    }

    @GetMapping(value = "/feed/{stock}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<MarketData> feed(@PathVariable("stock") String stock) {
        return rSocketRequester.route("feedMarketData")
                .data(new MarketDataRequest(stock))
                .retrieveFlux(MarketData.class);
    }

    @GetMapping(value = "/collect")
    public Publisher<Void> collect() {
        return rSocketRequester.route("collectMarketData")
                .data(getMarketData())
                .send();
    }

    private MarketData getMarketData() {
        return new MarketData("X", random.nextInt(10));
    }
}
```

### References
* RSocket Using Spring Boot, https://www.baeldung.com/spring-boot-rsocket
* Spring Boot Reference - RSocket, https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/html/spring-boot-features.html#boot-features-rsocket
* Spring Web Reactive | WebSocket, https://docs.spring.io/spring/docs/5.2.3.RELEASE/spring-framework-reference/web-reactive.html#rsocket-spring
* Getting Started with RSocket, https://wpanas.github.io/programming/2019/07/22/rsocket.html
