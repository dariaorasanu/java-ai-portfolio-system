package com.fiipractic.stocks.service;

import com.fiipractic.stocks.dto.StockHistoryResponse;
import com.fiipractic.stocks.dto.StockPriceHistoryDTO;
import com.fiipractic.stocks.model.Stock;
import com.fiipractic.stocks.model.StockPriceHistory;
import com.fiipractic.stocks.repository.StockPriceHistoryRepository;
import com.fiipractic.stocks.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockPriceHistoryService {

    private static final Logger log = LoggerFactory.getLogger(StockPriceHistoryService.class);

    private final StockPriceHistoryRepository priceHistoryRepository;
    private final StockRepository stockRepository;
    private final AlphaVantageClient alphaVantageClient;

    public StockPriceHistoryService(StockPriceHistoryRepository priceHistoryRepository,
                                    StockRepository stockRepository,
                                    AlphaVantageClient alphaVantageClient) {
        this.priceHistoryRepository = priceHistoryRepository;
        this.stockRepository = stockRepository;
        this.alphaVantageClient = alphaVantageClient;
    }

    /**
     * Fetch and store historical price data from Alpha Vantage
     *
     * @param symbol Stock symbol
     * @return Number of records saved
     */
    @Transactional
    public int fetchAndStoreHistoricalData(String symbol) {
        log.info("[HISTORY] Fetching and storing historical data for symbol: {}", symbol);

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        // Fetch data from Alpha Vantage
        Map<LocalDate, AlphaVantageClient.OHLCData> historicalData =
                alphaVantageClient.fetchHistoricalData(symbol);

        int savedCount = 0;
        for (Map.Entry<LocalDate, AlphaVantageClient.OHLCData> entry : historicalData.entrySet()) {
            LocalDate date = entry.getKey();
            AlphaVantageClient.OHLCData ohlcData = entry.getValue();

            // Skip if we already have data for this date
            if (priceHistoryRepository.existsByStockAndDate(stock, date)) {
                continue;
            }

            StockPriceHistory priceHistory = StockPriceHistory.builder()
                    .stock(stock)
                    .date(date)
                    .open(ohlcData.open())
                    .high(ohlcData.high())
                    .low(ohlcData.low())
                    .close(ohlcData.close())
                    .volume(ohlcData.volume())
                    .build();

            priceHistoryRepository.save(priceHistory);
            savedCount++;
        }

        log.info("[HISTORY] Saved {} new price records for {}", savedCount, symbol);
        return savedCount;
    }

    /**
     * Get historical price data for a stock
     * @param symbol Stock symbol
     * @param days Number of days to retrieve (optional, default all)
     * @return StockHistoryResponse with price data
     */
    @Transactional(readOnly = true)
    public StockHistoryResponse getStockHistory(String symbol, Integer days) {
        log.info("[HISTORY] Retrieving history for symbol: {}, days: {}", symbol, days);

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        List<StockPriceHistory> history;
        if (days != null && days > 0) {
            LocalDate startDate = LocalDate.now().minusDays(days);
            LocalDate endDate = LocalDate.now();
            history = priceHistoryRepository.findByStockAndDateBetweenOrderByDateDesc(stock, startDate, endDate);
        } else {
            history = priceHistoryRepository.findByStockOrderByDateDesc(stock);
        }

        List<StockPriceHistoryDTO> historyDTOs = history.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new StockHistoryResponse(symbol, historyDTOs);
    }

    /**
     * Get historical price data for a specific date range
     */
    @Transactional(readOnly = true)
    public StockHistoryResponse getStockHistoryByDateRange(String symbol, LocalDate startDate, LocalDate endDate) {
        log.info("[HISTORY] Retrieving history for symbol: {} from {} to {}", symbol, startDate, endDate);

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        List<StockPriceHistory> history =
                priceHistoryRepository.findByStockAndDateBetweenOrderByDateDesc(stock, startDate, endDate);

        List<StockPriceHistoryDTO> historyDTOs = history.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new StockHistoryResponse(symbol, historyDTOs);
    }

    private StockPriceHistoryDTO convertToDTO(StockPriceHistory entity) {
        return new StockPriceHistoryDTO(
                entity.getDate(),
                entity.getOpen(),
                entity.getHigh(),
                entity.getLow(),
                entity.getClose(),
                entity.getVolume()
        );
    }
}

