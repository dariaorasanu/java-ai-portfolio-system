package com.fiipractic.stocks.service;

import com.fiipractic.stocks.dto.BuyStockRequest;
import com.fiipractic.stocks.dto.CreatePortfolioRequest;
import com.fiipractic.stocks.dto.HoldingDTO;
import com.fiipractic.stocks.dto.PortfolioDTO;
import com.fiipractic.stocks.exception.UserNotOwnerOfPortfolioException;
import com.fiipractic.stocks.model.Portfolio;
import com.fiipractic.stocks.model.PortfolioHolding;
import com.fiipractic.stocks.model.Stock;
import com.fiipractic.stocks.repository.PortfolioHoldingRepository;
import com.fiipractic.stocks.repository.PortfolioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final StockService stockService;
    private final PriceRefreshPublisher priceRefreshPublisher;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            PortfolioHoldingRepository portfolioHoldingRepository,
                            StockService stockService,
                            PriceRefreshPublisher priceRefreshPublisher) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioHoldingRepository = portfolioHoldingRepository;
        this.stockService = stockService;
        this.priceRefreshPublisher = priceRefreshPublisher;
    }

    @Transactional
    public PortfolioDTO createPortfolio(String userId, CreatePortfolioRequest request) {
        Portfolio portfolio = Portfolio.builder()
                .name(request.getName())
                .description(request.getDescription())
                .holdings(new ArrayList<>())
                .userId(userId)
                .build();
        return toDTO(portfolioRepository.save(portfolio));
    }

    @Transactional(readOnly = true)
    public List<PortfolioDTO> getUserPortfolios(String userId) {
        return portfolioRepository.findByUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PortfolioDTO> getAllPortfolios() {
        return portfolioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PortfolioDTO buyStock(String userId, Long portfolioId, BuyStockRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .filter(p -> p.getUserId().equals(userId))
                .orElseThrow(() -> new UserNotOwnerOfPortfolioException("User is not owner of portfolio or portfolio does not exist"));

        // find existing stock by symbol, or create it if it doesn't exist yet
        Stock stock = stockService.findOrCreate(request.getSymbol());

        PortfolioHolding holding = PortfolioHolding.builder()
                .portfolio(portfolio)
                .stock(stock)
                .quantity(request.getQuantity())
                .purchasePrice(request.getPurchasePrice())
                .build();

        portfolioHoldingRepository.save(holding);
        portfolio.getHoldings().add(holding);

        return toDTO(portfolio);
    }

    private PortfolioDTO toDTO(Portfolio p) {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setHoldings(p.getHoldings().stream().map(this::toHoldingDTO).collect(Collectors.toList()));
        return dto;
    }

    private PortfolioDTO toDTO(Portfolio p) {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setHoldings(p.getHoldings().stream().map(this::toHoldingDTO).collect(Collectors.toList()));
        return dto;
    }

    private HoldingDTO toHoldingDTO(PortfolioHolding h) {
        return new HoldingDTO(
                h.getId(),
                h.getStock().getSymbol(),
                h.getQuantity(),
                h.getPurchasePrice(),
                h.getPurchasedAt()
        );
    }

    public record RefreshResponseDTO(String portfolioId, List<String> symbolsQueued, int totalSymbols, String message) {
    }
}