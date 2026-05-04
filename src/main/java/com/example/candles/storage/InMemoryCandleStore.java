package com.example.candles.storage;

import com.example.candles.model.Candle;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryCandleStore {

    private final Map<String, List<Candle>> store = new ConcurrentHashMap<>();

    public void save(String symbolInterval, Candle candle) {
        store.computeIfAbsent(symbolInterval, k -> new ArrayList<>()).add(candle);
    }

    public List<Candle> get(String symbolInterval) {
        return store.getOrDefault(symbolInterval, Collections.emptyList());
    }
}
