package com.fiipractic.stocks.dto;

import java.time.LocalDateTime;

public class PriceRefreshMessage {
    private String symbol;
    private LocalDateTime requestedAt;
    private String requestedBy;

    public PriceRefreshMessage() {
    }

    public PriceRefreshMessage(String symbol, LocalDateTime requestedAt, String requestedBy) {
        this.symbol = symbol;
        this.requestedAt = requestedAt;
        this.requestedBy = requestedBy;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }
}

