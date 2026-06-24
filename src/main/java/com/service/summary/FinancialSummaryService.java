package com.service.summary;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.service.analysis.AdrAnalysisService;
import com.model.analysis.AdrAnalysisResult;

import com.client.AdrClient;
import com.client.FinMindClient;
import com.client.FxRateClient;
import com.client.TwseClient;
import com.model.finmind.MonthlyRevenue;
import com.model.summary.FinancialSummary;

public class FinancialSummaryService {
	
	private final AdrAnalysisService
    adrAnalysisService =
    new AdrAnalysisService();

    private final TwseClient twseClient =
            new TwseClient();

    private final AdrClient adrClient =
            new AdrClient();

    private final FxRateClient fxRateClient =
            new FxRateClient();

    private final FinMindClient finMindClient =
            new FinMindClient();
    
    
    private BigDecimal getCurrent2330Price() {

        try {

            String json =
                    twseClient.getStockJson();

            if (json == null || json.isEmpty()) {
                return BigDecimal.ZERO;
            }

            int index =
                    json.indexOf("\"c\":\"2330\"");

            if (index == -1) {
                return BigDecimal.ZERO;
            }

            int zIndex =
                    json.indexOf("\"z\":\"", index);

            int start =
                    zIndex + 5;

            int end =
                    json.indexOf("\"", start);

            return new BigDecimal(
                    json.substring(start, end));

        } catch (Exception e) {

            return BigDecimal.ZERO;
        }
    }
    

    public FinancialSummary get2330Summary() {
    	BigDecimal twPrice =
    	        getCurrent2330Price();

    	BigDecimal usdRate =
    	        fxRateClient.getUsdTwdRate();

    	BigDecimal adrPrice =
    	        adrClient.getTsmEquivalentPrice(
    	                usdRate);

    	AdrAnalysisResult analysis =
    	        adrAnalysisService.analyze(
    	                twPrice,
    	                adrPrice);

        // 先放假資料測試 API

        MonthlyRevenue revenue =
                finMindClient.getLatestRevenue("2330");

        return new FinancialSummary(
                "2330",
                twPrice,
                analysis.getNetSpread(),
                revenue.getRevenue());
    }
}