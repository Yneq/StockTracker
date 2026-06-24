package com.dao;

import com.model.StockHistoryData;
import java.util.List;

public interface StockDao {
    void insertBatch(List<StockHistoryData> stockList) throws Exception;
    List<StockHistoryData> getAllHistory() throws Exception;
}