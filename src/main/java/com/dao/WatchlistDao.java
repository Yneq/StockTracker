package com.dao;

import java.util.List;
import com.model.Watchlist;

public interface WatchlistDao {

	//create
    boolean addStock(
            Long userId,
            String stockCode,
            String market);

    List<Watchlist> findByUserId(
            Long userId);
    
    //delete
    void deleteStock(
            Long userId,
            String stockCode);

    //read/update
    List<Watchlist> findAllDistinctStocks();
}