package com.fiipractic.stocks.repository;

import com.fiipractic.stocks.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByPortfolioId(Long portfolioId);
}
