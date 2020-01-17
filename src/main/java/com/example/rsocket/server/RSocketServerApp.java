package com.example.rsocket.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RSocketServerApp {

    public static void main(String[] args) {

        SpringApplication.run(RSocketServerApp.class, args);
        /*new SpringApplicationBuilder().main(RSocketServerApp.class)
                .sources(RSocketServerApp.class)
                .profiles("server")
                .run(args);*/
    }

}
