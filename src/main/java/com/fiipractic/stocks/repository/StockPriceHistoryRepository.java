package com.fiipractic.stocks.repository;

import com.fiipractic.stocks.model.Stock;
import com.fiipractic.stocks.model.StockPriceHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockPriceHistoryRepository extends JpaRepository<StockPriceHistory, Long> {

    List<StockPriceHistory> findByStockOrderByDateDesc(Stock stock);

    List<StockPriceHistory> findByStockAndDateBetweenOrderByDateDesc(Stock stock, LocalDate startDate, LocalDate endDate);

    Optional<StockPriceHistory> findByStockAndDate(Stock stock, LocalDate date);

    boolean existsByStockAndDate(Stock stock, LocalDate date);
}
