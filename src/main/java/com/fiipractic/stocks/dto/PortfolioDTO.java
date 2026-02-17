package com.fiipractic.stocks.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PortfolioDTO {
    private Long id;
    private String name;
    private String description;
    private List<StockDTO> stocks;
    private LocalDateTime createdAt;

    public PortfolioDTO() {
    }

    public PortfolioDTO(Long id, String name, String description, List<StockDTO> stocks, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stocks = stocks;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<StockDTO> getStocks() {
        return stocks;
    }

    public void setStocks(List<StockDTO> stocks) {
        this.stocks = stocks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
