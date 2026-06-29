# TaiwanStockTracker

[繁體中文](./README-tw.md) | English

A real-time Taiwan stock tracking system. Integrates Taiwan Stock Exchange (TWSE) real-time quotes and the FinMind open financial data API to provide live price broadcasting, monthly revenue trend analysis, ADR arbitrage spread calculation, and user-customizable stock watchlists.

<p align="center">
  <img src="./docs/images/1.png" width="170">
  <img src="./docs/images/2.png" width="300">
  <img src="./docs/images/3.png" width="300">
</p>

## Features

- **Real-time price broadcasting**: Pushes the latest stock prices every 5 seconds via WebSocket (STOMP over SockJS), no manual refresh needed
- **Personal watchlist**: Logged-in users can freely add/remove tracked stocks. The system automatically verifies whether a stock code actually exists (via the FinMind API) and determines whether it trades on TWSE (listed) or TPEx (OTC)
- **Dynamic fetch scope**: The scheduled task dynamically adjusts its fetch list based on stocks currently tracked by all users — once a stock is added, all users can see its real-time data
- **ADR arbitrage analysis**: Calculates the spread and arbitrage space between TSMC's ADR (US-listed) and its Taiwan stock price
- **Monthly revenue trend chart**: Visualizes company revenue trends using FinMind's monthly revenue data
- **JWT-based authentication**: Registration/login flow with bcrypt-hashed password storage

## Tech Stack

| Category | Technology |
|---|---|
| Backend Framework | Spring Boot 3.2.5 |
| Database | PostgreSQL (raw JDBC, no ORM) |
| Real-time Communication | WebSocket (STOMP + SockJS) |
| Authentication | JWT (jjwt) |
| Password Hashing | Spring Security Crypto (bcrypt) |
| Frontend | HTML + Tailwind CSS + Chart.js (vanilla JS, no framework) |
| External Data Sources | TWSE real-time quote API, FinMind open financial data API |


## Database Schema

<p align="center">
  <img src="./docs/images/er-diagram.png" width="500">
</p>

## Project Architecture

Uses a classic Layered Architecture (Controller → Service → DAO). Business logic is separated from the REST layer and database access. Instead of using Spring Data JPA, every database operation is implemented with hand-written SQL using PreparedStatement, providing full control over SQL execution.

```
com/
├── Main.java                     # Spring Boot application entry point
├── controller/                   # REST API layer
│   ├── AuthController            # User registration, login, authentication
│   ├── WatchlistController       # Watchlist CRUD APIs
│   └── StockController           # Stock-related APIs
├── service/                      # Business logic layer
│   ├── impl/
│   │   ├── AuthServiceImpl
│   │   ├── WatchlistServiceImpl
│   │   └── StockServiceImpl
├── dao/                          # Data access layer (hand-written SQL)
│   └── impl/
│       ├── UserDaoImpl
│       ├── WatchlistDaoImpl
│       └── StockDaoImpl
├── client/                       # External API integrations
│   ├── TwseClient                # TWSE real-time stock quotes
│   ├── FinMindClient             # Historical prices and monthly revenue
│   ├── AdrClient                 # US ADR quotes
│   └── FxRateClient              # Exchange rate data
├── model/                        # Entity and DTO classes
├── util/                         # Utilities (DBUtil, JwtUtil, Config...)
└── config/                       # Spring configuration (WebSocket, CORS, etc.)
```

## Setup

### Prerequisites

- JDK 17+
- Maven 3.8+
- PostgreSQL 14+

### 1. Database Setup

```sql
CREATE DATABASE stocktracker;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100)
);

CREATE TABLE stock_history (
    id SERIAL PRIMARY KEY,
    stock_code VARCHAR(10) NOT NULL,
    stock_name VARCHAR(50) NOT NULL,
    price NUMERIC,
    open_price NUMERIC,
    adr_price NUMERIC,
    arbitrage_space NUMERIC,
    trade_time TIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE watchlist (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    stock_code VARCHAR(10) NOT NULL,
    market VARCHAR(10) DEFAULT 'twse',
    UNIQUE(user_id, stock_code)
);
```

### 2. Environment Variables

This project does not hardcode any API keys in the source code. All sensitive credentials are injected via environment variables.

```bash
export FINMIND_TOKEN="your FinMind API Token"
```

> A FinMind Token can be obtained by registering at the [FinMind user info page](https://finmindtrade.com/analysis/#/account/user).
> If you suspect your token has been leaked, you can click "Update Token" on that page to instantly invalidate the old key.

Database connection settings should be adjusted in `DBUtil.java` or `application.properties` according to your environment.

### 3. Run the Project

```bash
mvn clean install
mvn spring-boot:run
```

Once started, open your browser and navigate to:

```
http://localhost:8080/login.html
```

## API Documentation

### Authentication `/api/auth`

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new account |
| POST | `/api/auth/login` | Log in, returns a JWT token |
| GET | `/api/auth/me` | Get current logged-in user info (requires token) |

### Watchlist `/api/watchlist` (requires `Authorization: Bearer <token>`)

| Method | Path | Description |
|---|---|---|
| GET | `/api/watchlist` | Get the current user's watchlist |
| POST | `/api/watchlist` | Add a tracked stock (body: `{"stockCode":"2330"}`), validates the code's authenticity and auto-detects the market |
| DELETE | `/api/watchlist/{stockCode}` | Remove a tracked stock |

### Stock Data

| Method | Path | Description |
|---|---|---|
| GET | `/api/history` | Get historical price records for all stocks in the database |
| GET | `/api/chart-overlay` | Get today/yesterday overlay chart data |
| GET | `/api/financial-summary` | Get financial summary (ADR arbitrage analysis, etc.) |
| GET | `/api/revenue-history` | Get monthly revenue trend history |
| GET | `/api/previous-close/{stockCode}` | Get the previous trading day's closing price for a given stock |

### WebSocket

```
Endpoint: /ws
Subscribe topic: /topic/stock
```

Broadcasts the latest stock prices every 5 seconds. Frontend example:

```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);
stompClient.connect({}, () => {
    stompClient.subscribe('/topic/stock', (message) => {
        const data = JSON.parse(message.body);
        console.log(data);
    });
});
```

## Security Notes

- All API keys are read from environment variables — **never hardcode keys in source code or commit them to version control**
- `.gitignore` already excludes files that may contain sensitive information, such as `.env` and `application-local.properties`
- Passwords are hashed with bcrypt before storage; plaintext passwords are never stored
- If you suspect any key has been leaked, immediately regenerate it from the corresponding service's dashboard

## License

This project is for learning and personal use only. Please refer to the official terms of service for the data sources used (TWSE, FinMind).