package com.fiipractic.stocks.service;

import com.fiipractic.stocks.dto.AddStockRequest;
import com.fiipractic.stocks.dto.CreatePortfolioRequest;
import com.fiipractic.stocks.dto.PortfolioDTO;
import com.fiipractic.stocks.dto.StockDTO;
import com.fiipractic.stocks.exception.PortfolioNotFoundException;
import com.fiipractic.stocks.exception.UserNotFoundException;
import com.fiipractic.stocks.model.Portfolio;
import com.fiipractic.stocks.model.Stock;
import com.fiipractic.stocks.model.User;
import com.fiipractic.stocks.repository.PortfolioRepository;
import com.fiipractic.stocks.repository.StockRepository;
import com.fiipractic.stocks.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    @Transactional
    public PortfolioDTO createPortfolio(String username, CreatePortfolioRequest portfolioRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        Portfolio portfolio = Portfolio.builder()
                .name(portfolioRequest.getName())
                .description(portfolioRequest.getDescription())
                .user(user).build();

        return convertToDTO(portfolioRepository.save(portfolio));
    }

    @Transactional(readOnly = true)
    public List<PortfolioDTO> getUserPortfolios(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return portfolioRepository.findByUserId(user.getId())
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public PortfolioDTO addStock(Long portfolioId, AddStockRequest stockRequest) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found: " + portfolioId));

        Stock stock = Stock.builder()
                .symbol(stockRequest.getSymbol().toUpperCase())
                .quantity(stockRequest.getQuantity())
                .purchasePrice(stockRequest.getPurchasePrice())
                .portfolio(portfolio).build();

        Stock savedStock = stockRepository.save(stock);
        portfolio.getStocks().add(savedStock);

        return convertToDTO(portfolio);
    }

    private PortfolioDTO convertToDTO(Portfolio p) {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setStocks(p.getStocks().stream().map(this::convertStockToDTO).collect(Collectors.toList()));
        return dto;
    }

    private StockDTO convertStockToDTO(Stock s) {
        StockDTO dto = new StockDTO();
        dto.setId(s.getId());
        dto.setSymbol(s.getSymbol());
        dto.setQuantity(s.getQuantity());
        dto.setPurchasePrice(s.getPurchasePrice());
        dto.setPurchasedAt(s.getPurchasedAt());
        return dto;
    }
}