package com.example.candles.service;

import com.example.candles.model.BidAskEvent;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AggregationService {

    private final ConcurrentMap<String, CandleAccumulator> active = new ConcurrentHashMap<>();

    public void process(BidAskEvent event, long interval) {
        double price = (event.bid() + event.ask()) / 2;
        long bucket = (event.timestamp() / interval) * interval;

        String key = event.symbol() + "_" + interval + "_" + bucket;

        active.computeIfAbsent(key, k -> new CandleAccumulator())
              .add(price);
    }

    public ConcurrentMap<String, CandleAccumulator> getActive() {
        return active;
    }
}
