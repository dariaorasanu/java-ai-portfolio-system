package com.fiipractic.stocks.dto;

/**
 * Response for fetch historical data operation
 */
public class FetchHistoryResponse {
    private String symbol;
    private int recordsSaved;
    private String message;

    public FetchHistoryResponse() {
    }

    public FetchHistoryResponse(String symbol, int recordsSaved, String message) {
        this.symbol = symbol;
        this.recordsSaved = recordsSaved;
        this.message = message;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getRecordsSaved() {
        return recordsSaved;
    }

    public void setRecordsSaved(int recordsSaved) {
        this.recordsSaved = recordsSaved;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

