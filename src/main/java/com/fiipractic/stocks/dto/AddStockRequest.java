package com.fiipractic.stocks.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AddStockRequest {
    @NotBlank(message = "Symbol is required")
    String symbol;

    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity;

    @DecimalMin(value = "0.01", message = "Purchase price must be positive")
    BigDecimal purchasePrice;
}
