package com.fiipractic.stocks.controller;

import com.fiipractic.stocks.dto.FetchHistoryResponse;
import com.fiipractic.stocks.dto.StockHistoryResponse;
import com.fiipractic.stocks.service.StockPriceHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * RController for managing stock price history
 */
@RestController
@RequestMapping("/api/stock-prices")
public class StockPriceHistoryController {

    private final StockPriceHistoryService stockPriceHistoryService;

    public StockPriceHistoryController(StockPriceHistoryService stockPriceHistoryService) {
        this.stockPriceHistoryService = stockPriceHistoryService;
    }

    /**
     * Fetch and store historical data from Alpha Vantage
     * POST /api/stocks/{symbol}/history/fetch?fullOutput=false
     */
    @PostMapping("/{symbol}/history/fetch")
    public ResponseEntity<FetchHistoryResponse> fetchHistoricalData(
            @PathVariable String symbol) {

        int recordsSaved = stockPriceHistoryService.fetchAndStoreHistoricalData(symbol);

        FetchHistoryResponse response = new FetchHistoryResponse(
                symbol,
                recordsSaved,
                "Historical data fetched and stored successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * fetch and store historical data for all stocks
     */
//    @PostMapping("/history/fetch-all")
//    public ResponseEntity<String> fetchHistoricalDataForAllStocks() {
//        int stockPriceHistoryService.fetchAndStoreHistoricalDataForAllStocks();
//        FetchHistoryResponse response = new FetchHistoryResponse(
//                "ALL",
//                recordsSaved,
//                "Historical data fetched and stored successfully"
//        );
//
//        return ResponseEntity.ok(response);
//    }
    /**
     * Get historical price data for a stock
     * GET /api/stocks/{symbol}/history?days=30
     */
    @GetMapping("/{symbol}/history")
    public ResponseEntity<StockHistoryResponse> getStockHistory(
            @PathVariable String symbol,
            @RequestParam(required = false) Integer days) {

        StockHistoryResponse response = stockPriceHistoryService.getStockHistory(symbol, days);
        return ResponseEntity.ok(response);
    }

    /**
     * Get historical price data for a specific date range
     * GET /api/stocks/{symbol}/history/range?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/{symbol}/history/range")
    public ResponseEntity<StockHistoryResponse> getStockHistoryByDateRange(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        StockHistoryResponse response = stockPriceHistoryService.getStockHistoryByDateRange(symbol, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}

