package com.example.candles.service;

import com.example.candles.model.Candle;

public class CandleAccumulator {
    private double open, high, low, close;
    private long volume;
    private boolean initialized = false;

    public synchronized void add(double price) {
        if (!initialized) {
            open = high = low = close = price;
            initialized = true;
        } else {
            high = Math.max(high, price);
            low = Math.min(low, price);
            close = price;
        }
        volume++;
    }

    public Candle toCandle(long time) {
        return new Candle(time, open, high, low, close, volume);
    }
}
