package com.service.analysis;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.model.analysis.AdrAnalysisResult;

public class AdrAnalysisService {

    private static final BigDecimal FOREX_COST =
            new BigDecimal("0.50");

    private static final BigDecimal LIQUIDITY_COST =
            new BigDecimal("3.50");

    private static final BigDecimal TIME_LAG_COST =
            new BigDecimal("7.35");

    public AdrAnalysisResult analyze(
            BigDecimal twPrice,
            BigDecimal adrEquivalentPrice) {

        BigDecimal rawSpread =
                adrEquivalentPrice
                        .subtract(twPrice)
                        .multiply(new BigDecimal("100"))
                        .divide(
                                twPrice,
                                2,
                                RoundingMode.HALF_UP);

        BigDecimal totalCost =
                FOREX_COST
                        .add(LIQUIDITY_COST)
                        .add(TIME_LAG_COST);

        BigDecimal netSpread =
                rawSpread.subtract(totalCost);

        return new AdrAnalysisResult(
                twPrice,
                adrEquivalentPrice,
                rawSpread,
                totalCost,
                netSpread);
    }
}