package com.fiipractic.stocks.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class PortfolioDTO {
    private Long id;
    private String name;
    private String description;
    private List<StockDTO> stocks;
    private LocalDateTime createdAt;
}
