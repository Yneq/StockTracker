package com.model;

import java.util.List;

public class StockChartResponse {
    private List<StockHistoryData> yesterdayData; // 淡灰色背景線（昨日完整走勢）
    private List<StockHistoryData> todayData;     // 粗彩色活線（今日 Live 走勢）

    public StockChartResponse(List<StockHistoryData> yesterdayData, List<StockHistoryData> todayData) {
        this.yesterdayData = yesterdayData;
        this.todayData = todayData;
    }

    // Getters and Setters
    public List<StockHistoryData> getYesterdayData() { return yesterdayData; }
    public void setYesterdayData(List<StockHistoryData> yesterdayData) { this.yesterdayData = yesterdayData; }
    public List<StockHistoryData> getTodayData() { return todayData; }
    public void setTodayData(List<StockHistoryData> todayData) { this.todayData = todayData; }
}