package com.fiipractic.stocks.dto;

import java.math.BigDecimal;

public record PositionSummaryDTO(
        String symbol,
        Integer totalQuantity,
        BigDecimal averagePurchasePrice,
        BigDecimal currentPrice,
        BigDecimal invested,
        BigDecimal currentValue,
        BigDecimal profitLoss,
        BigDecimal profitLossPercent) {
}
