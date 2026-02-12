package com.fiipractic.stocks.controller;

import com.fiipractic.stocks.dto.AddStockRequest;
import com.fiipractic.stocks.dto.CreatePortfolioRequest;
import com.fiipractic.stocks.dto.PortfolioDTO;
import com.fiipractic.stocks.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    public ResponseEntity<PortfolioDTO> createPortfolio(
            @RequestParam String username,
            @Valid @RequestBody CreatePortfolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(portfolioService.createPortfolio(username, request));
    }

    @GetMapping
    public ResponseEntity<List<PortfolioDTO>> getUserPortfolios(@RequestParam String username) {
        return ResponseEntity.ok(portfolioService.getUserPortfolios(username));
    }

    @PostMapping("/{portfolioId}/stocks")
    public ResponseEntity<PortfolioDTO> addStock(
            @PathVariable Long portfolioId,
            @Valid @RequestBody AddStockRequest request) {
        return ResponseEntity.ok(portfolioService.addStock(portfolioId, request));
    }
}
