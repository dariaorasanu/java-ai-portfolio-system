package com.fiipractic.stocks.controller;

import com.fiipractic.stocks.dto.StockDTO;
import com.fiipractic.stocks.service.PriceRefreshPublisher;
import com.fiipractic.stocks.service.StockService;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;
    private final PriceRefreshPublisher priceRefreshPublisher;

    public StockController(StockService stockService, PriceRefreshPublisher priceRefreshPublisher) {
        this.stockService = stockService;
        this.priceRefreshPublisher = priceRefreshPublisher;
    }

    @PostMapping
    public ResponseEntity<StockDTO> createStock(@RequestParam @NotBlank String symbol) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.createStock(symbol));
    }

    @GetMapping
    public ResponseEntity<List<StockDTO>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockDTO> getStock(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockDTO> updateStock(@PathVariable Long id,
                                                @RequestParam @NotBlank String symbol) {
        return ResponseEntity.ok(stockService.updateStock(id, symbol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAllPrices(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        priceRefreshPublisher.publishRefreshAll(userId);
        return ResponseEntity.accepted()
                .body(Map.of(
                        "status", "QUEUED",
                        "message", "Price refresh request for all stocks queued"
                ));
    }

    @PostMapping("/{symbol}/refresh")
    public ResponseEntity<Map<String, String>> refreshPrice(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String symbol) {
        String userId = jwt.getSubject();
        priceRefreshPublisher.publishRefresh(symbol, userId);
        return ResponseEntity.accepted()
                .body(Map.of(
                        "status", "QUEUED",
                        "symbol", symbol.toUpperCase(),
                        "message", "Price refresh request queued"
                ));
    }
}

