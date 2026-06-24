package com.dao;

import java.util.List;
import com.model.Watchlist;

public interface WatchlistDao {

    boolean addStock(
            Long userId,
            String stockCode,
            String market);

    List<Watchlist> findByUserId(
            Long userId);

    void deleteStock(
            Long userId,
            String stockCode);

    // 查詢所有使用者目前追蹤的股票（去重，含市場類別），讓定時任務知道要抓哪些股票、用哪個市場前綴
    List<Watchlist> findAllDistinctStocks();
}