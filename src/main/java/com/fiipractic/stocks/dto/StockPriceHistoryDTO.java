package com.fiipractic.stocks.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO representing a single day's OHLC price data
 */
public record StockPriceHistoryDTO(
        LocalDate date,
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        Long volume) {
}