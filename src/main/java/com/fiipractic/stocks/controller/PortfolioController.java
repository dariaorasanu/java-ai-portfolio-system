package com.fiipractic.stocks.controller;

import com.fiipractic.stocks.dto.BuyStockRequest;
import com.fiipractic.stocks.dto.CreatePortfolioRequest;
import com.fiipractic.stocks.dto.PortfolioDTO;
import com.fiipractic.stocks.dto.PortfolioValuationDTO;
import com.fiipractic.stocks.service.PortfolioService;
import com.fiipractic.stocks.service.PriceRefreshPublisher;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService, PriceRefreshPublisher priceRefreshPublisher) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    public ResponseEntity<PortfolioDTO> createPortfolio(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreatePortfolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(portfolioService.createPortfolio(jwt.getSubject(), request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'PREMIUM', 'ADMIN')")
    public ResponseEntity<List<PortfolioDTO>> getMyPortfolios(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(portfolioService.getUserPortfolios(jwt.getSubject()));
    }

    @PostMapping("/{portfolioId}/stocks")
    public ResponseEntity<PortfolioDTO> buyStock(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long portfolioId,
            @Valid @RequestBody BuyStockRequest request) {
        return ResponseEntity.ok(portfolioService.buyStock(jwt.getSubject(), portfolioId, request));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PortfolioDTO>> getAllPortfolios() {
        return ResponseEntity.ok(portfolioService.getAllPortfolios());
    }

    @GetMapping("/{portfolioId}/valuation")
    public ResponseEntity<PortfolioValuationDTO> getPortfolioValuation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long portfolioId) {
        return ResponseEntity.ok(portfolioService.calculateValuation(jwt.getSubject(), portfolioId));
    }

    @PostMapping("/{portfolioId}/refresh")
    public ResponseEntity<PortfolioService.RefreshResponseDTO> refreshPortfolioPrices(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long portfolioId,
            String correlationId
            ) {
        return ResponseEntity.ok(portfolioService.refreshPortfolioPrices(jwt.getSubject(), portfolioId, correlationId));
    }
}
