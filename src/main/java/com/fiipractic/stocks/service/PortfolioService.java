package com.fiipractic.stocks.service;

import com.fiipractic.stocks.dto.BuyStockRequest;
import com.fiipractic.stocks.dto.CreatePortfolioRequest;
import com.fiipractic.stocks.dto.HoldingDTO;
import com.fiipractic.stocks.dto.PortfolioDTO;
import com.fiipractic.stocks.exception.PortfolioNotFoundException;
import com.fiipractic.stocks.exception.UserNotFoundException;
import com.fiipractic.stocks.model.Portfolio;
import com.fiipractic.stocks.model.PortfolioHolding;
import com.fiipractic.stocks.model.Stock;
import com.fiipractic.stocks.model.User;
import com.fiipractic.stocks.repository.PortfolioHoldingRepository;
import com.fiipractic.stocks.repository.PortfolioRepository;
import com.fiipractic.stocks.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioHoldingRepository holdingRepository;
    private final StockService stockService;

    public PortfolioService(PortfolioRepository portfolioRepository,
                           UserRepository userRepository,
                           PortfolioHoldingRepository holdingRepository,
                           StockService stockService) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.holdingRepository = holdingRepository;
        this.stockService = stockService;
    }

    @Transactional
    public PortfolioDTO createPortfolio(String username, CreatePortfolioRequest portfolioRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        Portfolio portfolio = Portfolio.builder()
                .name(portfolioRequest.getName())
                .description(portfolioRequest.getDescription())
                .user(user)
                .holdings(new ArrayList<>())
                .build();

        return toDTO(portfolioRepository.save(portfolio));
    }

    @Transactional(readOnly = true)
    public List<PortfolioDTO> getUserPortfolios(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return portfolioRepository.findByUserId(user.getId())
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
    public PortfolioDTO buyStock(Long portfolioId, BuyStockRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found: " + portfolioId));

        // Find or create the stock in the catalog
        Stock stock = stockService.findOrCreate(request.getSymbol());

        // Create a new holding (purchase lot)
        PortfolioHolding holding = PortfolioHolding.builder()
                .portfolio(portfolio)
                .stock(stock)
                .quantity(request.getQuantity())
                .purchasePrice(request.getPurchasePrice())
                .build();

        holdingRepository.save(holding);
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

    private HoldingDTO toHoldingDTO(PortfolioHolding h) {
        return new HoldingDTO(
            h.getId(),
            h.getStock().getSymbol(),
            h.getQuantity(),
            h.getPurchasePrice(),
            h.getPurchasedAt()
        );
    }
}