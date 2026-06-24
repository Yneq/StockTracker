package com.model.finmind;

import java.math.BigDecimal;

public class MonthlyRevenue {

    private String date;
    private String stockId;
    private BigDecimal revenue;

    public MonthlyRevenue(
            String date,
            String stockId,
            BigDecimal revenue) {

        this.date = date;
        this.stockId = stockId;
        this.revenue = revenue;
    }

    public String getDate() {
        return date;
    }

    public String getStockId() {
        return stockId;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }
}