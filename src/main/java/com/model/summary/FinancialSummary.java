package com.model.summary;

import java.math.BigDecimal;

public class FinancialSummary {

    private String stockCode;

    private BigDecimal currentPrice;

    private BigDecimal adrPremium;

    private BigDecimal monthlyRevenue;

    public FinancialSummary(
            String stockCode,
            BigDecimal currentPrice,
            BigDecimal adrPremium,
            BigDecimal monthlyRevenue) {

        this.stockCode = stockCode;
        this.currentPrice = currentPrice;
        this.adrPremium = adrPremium;
        this.monthlyRevenue = monthlyRevenue;
    }

    public String getStockCode() {
        return stockCode;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getAdrPremium() {
        return adrPremium;
    }

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }
}