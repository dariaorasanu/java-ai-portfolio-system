package com.fiipractic.stocks.dto;

/**
 * Response for fetch historical data operation
 */
public record FetchHistoryResponse(String symbol, int recordsSaved, String message) {
}

