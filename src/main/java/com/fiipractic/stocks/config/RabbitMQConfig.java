package com.fiipractic.stocks.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PRICE_REFRESH_QUEUE = "stock.price.refresh";
    public static final String PRICE_EXCHANGE = "stock.price.exchange";
    public static final String ROUTING_KEY = "stock.refresh";

    @Bean
    public Queue priceRefreshQueue() {
        return QueueBuilder.durable(PRICE_REFRESH_QUEUE).build();
    }

    @Bean
    public DirectExchange priceExchange() {
        return new DirectExchange(PRICE_EXCHANGE);
    }

    @Bean
    public Binding priceBinding() {
        return BindingBuilder
                .bind(priceRefreshQueue())
                .to(priceExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}

