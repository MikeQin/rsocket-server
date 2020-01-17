package com.example.rsocket.model;

import lombok.Value;

@Value
public class MarketDataError {
    private String message;

    public MarketDataError(Exception e) {
        this.message = e.getMessage();
    }
}
