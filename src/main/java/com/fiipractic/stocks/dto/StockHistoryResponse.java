package com.fiipractic.stocks.dto;

import java.util.List;

/**
 * Response containing historical price data for a stock
 */
public class StockHistoryResponse {
    private String symbol;
    private List<StockPriceHistoryDTO> history;
    private int totalDays;

    public StockHistoryResponse() {
    }

    public StockHistoryResponse(String symbol, List<StockPriceHistoryDTO> history) {
        this.symbol = symbol;
        this.history = history;
        this.totalDays = history != null ? history.size() : 0;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<StockPriceHistoryDTO> getHistory() {
        return history;
    }

    public void setHistory(List<StockPriceHistoryDTO> history) {
        this.history = history;
        this.totalDays = history != null ? history.size() : 0;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }
}

