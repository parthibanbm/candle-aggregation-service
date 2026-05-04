
package com.example.candles.controller;

import com.example.candles.model.Candle;
import com.example.candles.storage.InMemoryCandleStore;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/history")
public class CandleController {

    private final InMemoryCandleStore store;

    public CandleController(InMemoryCandleStore store) {
        this.store = store;
    }

    @GetMapping
    public Map<String, Object> getHistory(
            @RequestParam String symbol,
            @RequestParam String interval,
            @RequestParam long from,
            @RequestParam long to
    ) {
        String key = symbol + "_" + interval;

        List<Candle> candles = store.get(key);

        if (candles == null) {
            candles = Collections.emptyList();
        }

        List<Candle> filtered = candles.stream()
                .filter(c -> c.time() >= from && c.time() <= to)
                .collect(Collectors.toList());

        Map<String, Object> res = new HashMap<>();
        res.put("s", "ok");
        res.put("t", filtered.stream().map(Candle::time).toList());
        res.put("o", filtered.stream().map(Candle::open).toList());
        res.put("h", filtered.stream().map(Candle::high).toList());
        res.put("l", filtered.stream().map(Candle::low).toList());
        res.put("c", filtered.stream().map(Candle::close).toList());
        res.put("v", filtered.stream().map(Candle::volume).toList());

        return res;
    }
}
