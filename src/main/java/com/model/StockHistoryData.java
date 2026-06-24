package com.model;

import java.math.BigDecimal;

public class StockHistoryData {
    private int id;
    private String stockCode;
    private String stockName;
    private BigDecimal price;
    private BigDecimal openPrice;
    private BigDecimal adrPrice;         // 對接新欄位
    private BigDecimal arbitrageSpace;   // 對接新欄位
    private String tradeTime;
    private String createdAt;

    // 完整建構子
    public StockHistoryData(int id, String stockCode, String stockName, BigDecimal price, 
                            BigDecimal openPrice, BigDecimal adrPrice, BigDecimal arbitrageSpace, 
                            String tradeTime, String createdAt) {
        this.id = id;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.price = price;
        this.openPrice = openPrice;
        this.adrPrice = adrPrice;
        this.arbitrageSpace = arbitrageSpace;
        this.tradeTime = tradeTime;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public String getStockCode() { return stockCode; }
    public String getStockName() { return stockName; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getOpenPrice() { return openPrice; }
    public BigDecimal getAdrPrice() { return adrPrice; }
    public BigDecimal getArbitrageSpace() { return arbitrageSpace; }
    public String getTradeTime() { return tradeTime; }
    public String getCreatedAt() { return createdAt; }
}