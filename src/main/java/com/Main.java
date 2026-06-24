package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.client.FinMindClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.model.StockChartResponse;
import com.model.StockHistoryData;
import com.model.analysis.AdrAnalysisResult;
import com.model.finmind.MonthlyRevenue;
import com.service.StockService;
import com.service.analysis.AdrAnalysisService;
import com.model.summary.FinancialSummary;
import com.service.summary.FinancialSummaryService;
import com.util.PasswordUtil;
import com.model.finmind.RevenuePoint;

@SpringBootApplication
@EnableScheduling
@RestController
@CrossOrigin
public class Main {
    
	private final Gson gson = new Gson();
	private final FinMindClient
    finMindClient =
    new FinMindClient();
    // 🎯 Spring 會自動去找到剛剛加上 @Service 的 StockServiceImpl 並精準注入，不再死循環
    @Autowired
    private StockService stockService;
    private final FinancialSummaryService financialSummaryService =new FinancialSummaryService();

    public static void main(String[] args) {
//    	System.out.println(PasswordUtil.encode("123456"));
    	
    	
        SpringApplication.run(Main.class, args);
        FinMindClient finMindClient=new FinMindClient();

        MonthlyRevenue revenue=finMindClient.getLatestRevenue("2330");

        System.out.println(
                "最新月份: "
                + revenue.getDate());

        System.out.println("月營收: "+revenue.getRevenue());

    }
    

    // 🔄 每 5 秒自動執行的定時任務
    @Scheduled(fixedRate = 5000)
    public void autoFetchTask() {
        try {
            stockService.downloadAndStoreStockData();
        } catch (Exception e) {
            System.err.println("⚠️ 定時抓取發生嚴重異常！");
            e.printStackTrace();
        }
    }

    // 📊 讀取 API
    @GetMapping("/api/history")
    public List<StockHistoryData> getHistory() {
        try {
            return stockService.fetchDisplayData();
        } catch (Exception e) {
            System.err.println("❌ 讀取 API 失敗: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }
    
 // 📊 雙日重疊圖表專用 API
    @GetMapping("/api/chart-overlay")
    public StockChartResponse getChartOverlay() {
        try {
            // 1. 撈出資料庫裡所有的歷史紀錄
            List<StockHistoryData> allData = stockService.fetchDisplayData();
            
            // 2. 透過簡單的邏輯區分「今天」與「昨天」
            // 實務上可以用 SQL 的日期判斷，這裡示範用資料庫最後幾筆進行切分模擬
            int totalSize = allData.size();
            
            // 假設每輪定時任務抓 3 筆，我們切出前一天的完整波形當背景
            List<StockHistoryData> yesterday = new ArrayList<>();
            List<StockHistoryData> today = new ArrayList<>();
            
            if (totalSize > 6) {
                yesterday = allData.subList(0, totalSize / 2);
                today = allData.subList(totalSize / 2, totalSize);
            } else {
                today = allData;
            }
            
            return new StockChartResponse(yesterday, today);
        } catch (Exception e) {
            System.err.println("❌ 讀取圖表重疊數據失敗: " + e.getMessage());
            return new StockChartResponse(java.util.Collections.emptyList(), java.util.Collections.emptyList());
        }
    }
    @GetMapping("/api/financial-summary")
    public FinancialSummary getFinancialSummary() {

        return financialSummaryService
                .get2330Summary();
    }
    
    @GetMapping("/api/revenue-history")
    public List<RevenuePoint> getRevenueHistory() {

        return finMindClient
                .getRevenueHistory("2330");
    }
    @GetMapping("/api/previous-close/{stockCode}")
    public BigDecimal getPreviousClose(@PathVariable String stockCode) {
        try {
            // 使用你現有的 FinMindClient
            String dateStr = java.time.LocalDate.now().minusDays(1).toString();
            
            String url = "https://api.finmindtrade.com/api/v4/data?dataset=TaiwanStockPrice" +
                         "&data_id=" + stockCode +
                         "&start_date=" + dateStr + "&end_date=" + dateStr +
                         "&token=" + URLEncoder.encode(com.util.FinMindConfig.getToken(), StandardCharsets.UTF_8);

            String json = com.util.HttpUtil.fetchJson(url);
            JsonObject root = gson.fromJson(json, JsonObject.class);
            JsonArray data = root.getAsJsonArray("data");

            if (data != null && data.size() > 0) {
                return data.get(0).getAsJsonObject().get("close").getAsBigDecimal();
            }
        } catch (Exception e) {
            System.err.println("抓昨日收盤失敗: " + stockCode + " → " + e.getMessage());
        }
        return null;
    }

}