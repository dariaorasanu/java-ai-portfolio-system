# Java & AI Stock Portfolio System

A production-ready stock portfolio management system built step by step during the FII PRACTIC training program.

This project started from a basic portfolio management API and was gradually extended with authentication, asynchronous stock price refresh, portfolio valuation, and centralized structured logging.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- PostgreSQL
- Keycloak
- RabbitMQ
- LogBull
- Docker Compose
- Maven

## Project Overview

The application allows authenticated users to manage stock portfolios, buy stocks, refresh live prices, and calculate portfolio valuation.

The project was developed progressively through multiple training sessions:

- **Training 1** – basic portfolio management
- **Training 2** – Keycloak authentication with JWT
- **Training 3** – real-time stock price refresh with RabbitMQ and portfolio valuation
- **Training 4** – centralized structured logging with LogBull and correlation ID tracing

## Main Features

- Secure REST API with JWT authentication
- Portfolio ownership validation
- Role-based access control
- Live stock price refresh using Alpha Vantage
- Asynchronous processing with RabbitMQ
- Portfolio valuation with profit/loss calculation
- Centralized structured logs with LogBull
- Correlation ID tracing across HTTP request, queue, and consumer flow

## Training Progression

### Training 1
Initial backend structure for:
- portfolios
- stocks
- holdings
- basic CRUD-style operations

### Training 2
Authentication and authorization were migrated to Keycloak.

Main changes:
- removed the local `User` entity
- users are managed by Keycloak
- protected endpoints require JWT authentication
- portfolio ownership is based on the Keycloak user ID (`sub`)
- role-based access control was added with Spring Security

### Training 3
The application was extended with live stock prices and asynchronous processing.

Main changes:
- stock entities now store `currentPrice` and `lastPriceUpdate`
- Alpha Vantage integration was added for real stock prices
- RabbitMQ was introduced to queue refresh requests
- a producer-consumer flow was implemented for stock price refresh
- portfolio valuation was added using current prices and weighted average purchase cost

### Training 4
The project was extended with observability and centralized structured logging.

Main changes:
- LogBull was added for centralized log collection
- MDC-based structured logging was introduced
- correlation IDs are generated and propagated through the refresh flow
- improved visibility was added for:
    - request tracing
    - error monitoring
    - portfolio operations
    - slow processing detection

## Architecture Overview

### Authentication flow
- Keycloak issues JWT access tokens
- Spring Security validates the token
- the authenticated user is extracted from the JWT
- access is controlled based on roles and portfolio ownership

### Stock refresh flow
1. A user sends a stock refresh request
2. the controller generates a correlation ID
3. the request is logged with structured MDC fields
4. a message is published to RabbitMQ
5. the consumer processes the message asynchronously
6. the latest stock price is fetched from Alpha Vantage
7. the stock is updated in the database
8. all steps can be traced in LogBull using the same correlation ID

## Infrastructure

The application uses the following services:
- PostgreSQL
- Keycloak
- RabbitMQ
- LogBull

All services are started with Docker Compose.
