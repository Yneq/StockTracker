package com.model.analysis;

import java.math.BigDecimal;

public class AdrAnalysisResult {

    private BigDecimal twPrice;
    private BigDecimal adrEquivalentPrice;

    private BigDecimal rawSpread;
    private BigDecimal totalCost;
    private BigDecimal netSpread;

    public AdrAnalysisResult(
            BigDecimal twPrice,
            BigDecimal adrEquivalentPrice,
            BigDecimal rawSpread,
            BigDecimal totalCost,
            BigDecimal netSpread) {

        this.twPrice = twPrice;
        this.adrEquivalentPrice = adrEquivalentPrice;
        this.rawSpread = rawSpread;
        this.totalCost = totalCost;
        this.netSpread = netSpread;
    }

    public BigDecimal getTwPrice() {
        return twPrice;
    }

    public BigDecimal getAdrEquivalentPrice() {
        return adrEquivalentPrice;
    }

    public BigDecimal getRawSpread() {
        return rawSpread;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public BigDecimal getNetSpread() {
        return netSpread;
    }
}