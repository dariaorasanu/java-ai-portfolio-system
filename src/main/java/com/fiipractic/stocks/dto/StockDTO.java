package com.fiipractic.stocks.dto;

public class StockDTO {
    private Long id;
    private String symbol;

    public StockDTO() {
    }

    public StockDTO(Long id, String symbol) {
        this.id = id;
        this.symbol = symbol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
}
