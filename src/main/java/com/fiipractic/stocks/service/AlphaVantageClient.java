package com.fiipractic.stocks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AlphaVantageClient {

    private static final Logger log = LoggerFactory.getLogger(AlphaVantageClient.class);

    @Value("${alphavantage.base-url:https://www.alphavantage.co/query}")
    private String BASE_URL;

    @Value("${alphavantage.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetches the most recent daily closing price for the given symbol.
     * Endpoint: TIME_SERIES_DAILY, outputsize=compact
     * Docs: https://www.alphavantage.co/documentation/#daily
     */
    public BigDecimal fetchLatestPrice(String symbol) {
        String url = BASE_URL + "?function=TIME_SERIES_DAILY"
                + "&symbol=" + symbol
                + "&outputsize=compact"
                + "&apikey=" + apiKey;

        log.info("[ALPHA_VANTAGE] Calling API for symbol [{}]", symbol);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) {
            throw new RuntimeException("Empty response from Alpha Vantage for: " + symbol);
        }

        // Alpha Vantage returns an "Information" or "Note" key when rate-limited
        if (response.containsKey("Information") || response.containsKey("Note")) {
            String message = response.containsKey("Information")
                    ? (String) response.get("Information")
                    : (String) response.get("Note");
            throw new RuntimeException("Alpha Vantage rate limit or error: " + message);
        }

        Map<String, Object> timeSeries = (Map<String, Object>) response.get("Time Series (Daily)");
        if (timeSeries == null || timeSeries.isEmpty()) {
            throw new RuntimeException("No time series data for symbol: " + symbol);
        }

        // the first key is the most recent date
        String latestDate = timeSeries.keySet().iterator().next();
        Map<String, String> latestBar = (Map<String, String>) timeSeries.get(latestDate);
        String closePrice = latestBar.get("4. close");

        log.info("[ALPHA_VANTAGE] Fetched price for [{}]: ${}", symbol, closePrice);
        return new BigDecimal(closePrice);
    }

    /**
     * Fetches complete historical daily OHLC data for the given symbol.
     * Returns a map where key = date string (YYYY-MM-DD) and value = map of OHLC data.
     * Endpoint: TIME_SERIES_DAILY, outputsize=compact
     * Docs: https://www.alphavantage.co/documentation/#daily
     */
    public Map<LocalDate, OHLCData> fetchHistoricalData(String symbol) {
        String url = BASE_URL + "?function=TIME_SERIES_DAILY"
                + "&symbol=" + symbol
                + "&outputsize=" + "compact"
                + "&apikey=" + apiKey;

        log.info("[ALPHA_VANTAGE] Fetching historical data for symbol [{}], outputsize={}", symbol, "compact");

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) {
            throw new RuntimeException("Empty response from Alpha Vantage for: " + symbol);
        }

        // Alpha Vantage returns an "Information" or "Note" key when rate-limited
        if (response.containsKey("Information") || response.containsKey("Note")) {
            String message = response.containsKey("Information")
                    ? (String) response.get("Information")
                    : (String) response.get("Note");
            throw new RuntimeException("Alpha Vantage rate limit or error: " + message);
        }

        Map<String, Object> timeSeries = (Map<String, Object>) response.get("Time Series (Daily)");
        if (timeSeries == null || timeSeries.isEmpty()) {
            throw new RuntimeException("No time series data for symbol: " + symbol);
        }

        // parse the time series data to an OHLC structure
        Map<LocalDate, OHLCData> historicalData = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : timeSeries.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey());
            Map<String, String> dailyData = (Map<String, String>) entry.getValue();

            OHLCData ohlcData = new OHLCData(
                    new BigDecimal(dailyData.get("1. open")),
                    new BigDecimal(dailyData.get("2. high")),
                    new BigDecimal(dailyData.get("3. low")),
                    new BigDecimal(dailyData.get("4. close")),
                    Long.parseLong(dailyData.get("5. volume"))
            );

            historicalData.put(date, ohlcData);
        }

        log.info("[ALPHA_VANTAGE] Fetched {} days of historical data for [{}]", historicalData.size(), symbol);
        return historicalData;
    }

    public record OHLCData(BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, Long volume) {
    }
}
