package com.controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.client.FinMindClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.model.StockChartResponse;
import com.model.StockHistoryData;
import com.model.finmind.RevenuePoint;
import com.model.summary.FinancialSummary;
import com.service.StockService;
import com.service.summary.FinancialSummaryService;

/**
 * 股票相關的對外 API 端點。
 * 這個檔案是從 Main.java 拆出來的，原本這些 @GetMapping
 * 跟 main() 進入點寫在同一個檔案裡，改動 API 時容易不小心
 * 動到啟動程式的部分。拆開後職責分離：
 * Main.java 只負責啟動，這裡只負責對外 API。
 */
@RestController
@CrossOrigin
public class StockController {

    private final Gson gson = new Gson();
    private final FinMindClient finMindClient = new FinMindClient();
    private final FinancialSummaryService financialSummaryService = new FinancialSummaryService();

    // 🎯 Spring 會自動找到加上 @Service 的 StockServiceImpl 並注入
    @Autowired
    private StockService stockService;

    // 📊 讀取歷史股價資料
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

    // 📊 財務摘要（含 ADR 套利分析）
    @GetMapping("/api/financial-summary")
    public FinancialSummary getFinancialSummary() {
        return financialSummaryService.get2330Summary();
    }

    // 📈 月營收歷史趨勢
    @GetMapping("/api/revenue-history")
    public List<RevenuePoint> getRevenueHistory() {
        return finMindClient.getRevenueHistory("2330");
    }

    // 📅 前一交易日收盤價
    @GetMapping("/api/previous-close/{stockCode}")
    public BigDecimal getPreviousClose(@PathVariable String stockCode) {
        try {
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