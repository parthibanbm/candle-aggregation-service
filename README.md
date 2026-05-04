# Candle Aggregation Service

## 📌 Overview

This project implements a backend Java service that ingests a continuous stream of bid/ask market data and aggregates it into OHLC (Open, High, Low, Close) candlestick format. The service supports multiple symbols and configurable time intervals, and exposes a REST API for retrieving historical candle data suitable for charting libraries like TradingView Lightweight Charts.

## 🚀 Features

* Real-time ingestion of `BidAskEvent`
* OHLC candle aggregation for:

  * Multiple symbols (e.g., BTC-USD, ETH-USD)
  * Multiple intervals (1s, 5s, 1m, etc.)
* Thread-safe and concurrent processing
* In-memory storage (extensible to DB)
* REST API for historical data:

  ```
  GET /history?symbol=BTC-USD&interval=1m&from=...&to=...
  ```
* Scheduled candle finalization
* Modular and extensible design

---

## 🧠 Architecture

```
Event Generator / Stream
          ↓
   Ingestion Layer
          ↓
 Aggregation Engine
 (Concurrent + Bucketed)
          ↓
   Storage Layer
          ↓
      REST API
```

---

## 📦 Data Models

```java
record BidAskEvent(String symbol, double bid, double ask, long timestamp) {}

record Candle(long time, double open, double high, double low, double close, long volume) {}
```

---

## ⚙️ Assumptions & Trade-offs

### Assumptions

* Price used for aggregation = '(bid + ask) / 2'
* Volume is synthetic (tick count)
* Events are mostly time-ordered (minor delays tolerated)

### Trade-offs

* **In-memory storage** chosen for simplicity and speed
  → Not durable across restarts
* **Synchronized accumulator** used instead of complex locking
  → Simpler and efficient for this scale
* **Batch candle flush via scheduler**
  → Slight delay in final candle availability
* No distributed coordination (single-instance design)

---

## 🧵 Concurrency Design

* `ConcurrentHashMap` for candle buckets
* Thread-safe `CandleAccumulator`
* Bounded `ThreadPoolExecutor` to:

  * Prevent overload
  * Provide backpressure
* Event queue (`BlockingQueue`) for async processing

---

## 🗄️ Storage

### Current:

* In-memory (`ConcurrentMap<String, List<Candle>>`)

### Easily Extendable To:

* PostgreSQL / TimescaleDB
* Redis (for caching hot queries)

---

## 📡 API

### GET /history

#### Example:

```
/history?symbol=BTC-USD&interval=1m&from=1620000000&to=1620000600
```

#### Response:

```json
{
  "s": "ok",
  "t": [1620000000],
  "o": [29500.5],
  "h": [29510.0],
  "l": [29490.0],
  "c": [29505.0],
  "v": [10]
}
```

---

## 🛠️ How to Run

### Prerequisites

* Java 17+
* Maven

### Steps

```bash
git clone <repo-url>
cd candle-aggregation-service
mvn clean install
mvn spring-boot:run
```

---

## 🧪 Running Tests

```bash
mvn test
```

### Test Coverage Includes:

* Candle aggregation logic
* Bucket alignment
* Edge cases (first tick, multiple updates)

---

## 📊 Performance Considerations

* O(1) aggregation per event
* Minimal locking scope
* No blocking DB writes in hot path
* Bounded queues to avoid memory issues

---

## 🔍 Observability

* Logging for:

  * Event ingestion
  * Candle creation
* Basic health endpoint (`/actuator/health` if Spring Boot)

---

## ✨ Bonus Features (Optional Enhancements)

* WebSocket for live candles
* Kafka-based ingestion
* Persistent storage (PostgreSQL / TimescaleDB)
* Redis caching for `/history`
* Multi-instance scaling via partitioning (by symbol)
* Backfill / replay support

---

## 📌 Future Improvements

* Handle out-of-order events more robustly
* Add watermarking strategy
* Horizontal scaling with Kafka partitions
* Add Grafana + Prometheus monitoring

---

## 👨‍💻 Author Notes

This implementation prioritizes:

* Simplicity
* Thread safety
* Performance under moderate load

It avoids overengineering (e.g., distributed locks, heavy DB transactions) while remaining extensible for production-grade systems.


