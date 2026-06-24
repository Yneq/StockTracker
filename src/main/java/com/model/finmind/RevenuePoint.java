package com.model.finmind;

import java.math.BigDecimal;

public class RevenuePoint {

    private String date;

    private BigDecimal revenue;

    public RevenuePoint(
            String date,
            BigDecimal revenue) {

        this.date = date;
        this.revenue = revenue;
    }

    public String getDate() {
        return date;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }
}