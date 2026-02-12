package com.fiipractic.stocks.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class StockDTO {
    private Long id;
    private String symbol;
    private Integer quantity;
    private BigDecimal purchasePrice;
    private LocalDateTime purchasedAt;
}
