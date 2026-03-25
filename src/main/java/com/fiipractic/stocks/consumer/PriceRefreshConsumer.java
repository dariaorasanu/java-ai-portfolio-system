package com.fiipractic.stocks.consumer;

import com.fiipractic.stocks.config.RabbitMQConfig;
import com.fiipractic.stocks.dto.PriceRefreshMessage;
import com.fiipractic.stocks.model.Stock;
import com.fiipractic.stocks.repository.StockRepository;
import com.fiipractic.stocks.service.AlphaVantageClient;

import com.rabbitmq.client.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class PriceRefreshConsumer {

    private static final Logger log = LoggerFactory.getLogger(PriceRefreshConsumer.class);

    private final AlphaVantageClient alphaVantageClient;
    private final StockRepository stockRepository;

    public PriceRefreshConsumer(AlphaVantageClient alphaVantageClient, StockRepository stockRepository) {
        this.alphaVantageClient = alphaVantageClient;
        this.stockRepository = stockRepository;
    }

    /**
     * Queue listener to process incoming refresh messages.
     * This prevents exceeding Alpha Vantage's rate limit.
     * @apiNote {@code concurrency = "1"} ensures only one message is processed at a time to follow
     * Alpha Vantage's free tier limit of 1 concurrent request
     */
    @RabbitListener(
            queues = RabbitMQConfig.PRICE_REFRESH_QUEUE,
            concurrency = "1",
            ackMode = "MANUAL"
    )
    public void onPriceRefreshRequest(
            PriceRefreshMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("[CONSUMER] Processing [{}] requested by [{}] on thread: {}",
                message.getSymbol(), message.getRequestedBy(),
                Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            // fetch latest price from Alpha Vantage
            BigDecimal price = alphaVantageClient.fetchLatestPrice(message.getSymbol());

            // update the stock entity with current price
            Stock stock = stockRepository.findBySymbol(message.getSymbol())
                    .orElseThrow(() -> new RuntimeException("Stock not found: " + message.getSymbol()));

            stock.setCurrentPrice(price);
            stock.setLastPriceUpdate(LocalDateTime.now());
            stockRepository.save(stock);

            log.info("[CONSUMER] Updated price for [{}] to ${} at {}",
                    message.getSymbol(), price, stock.getLastPriceUpdate());

            // acknowledge message, successfull message
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[CONSUMER] Failed to fetch price for [{}]: {}",
                    message.getSymbol(), e.getMessage());

            // negative acknowledge - drop the message, might be nice to queue in a dead letter queue for later analysis
            channel.basicNack(deliveryTag, false, false);
        }
    }
}

