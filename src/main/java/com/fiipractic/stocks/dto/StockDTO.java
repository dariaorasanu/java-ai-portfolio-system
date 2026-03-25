package com.fiipractic.stocks.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockDTO {
    private Long id;
    private String symbol;
    private BigDecimal currentPrice;
    private LocalDateTime lastPriceUpdate;

    public StockDTO() {
    }

    public StockDTO(Long id, String symbol) {
        this.id = id;
        this.symbol = symbol;
    }

    public StockDTO(Long id, String symbol, BigDecimal currentPrice, LocalDateTime lastPriceUpdate) {
        this.id = id;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.lastPriceUpdate = lastPriceUpdate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public LocalDateTime getLastPriceUpdate() { return lastPriceUpdate; }
    public void setLastPriceUpdate(LocalDateTime lastPriceUpdate) { this.lastPriceUpdate = lastPriceUpdate; }
}
