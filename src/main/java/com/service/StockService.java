package com.service;

import com.model.StockHistoryData;
import com.exception.StockDataException;
import java.util.List;

public interface StockService {
    void downloadAndStoreStockData() throws StockDataException;
    List<StockHistoryData> fetchDisplayData() throws StockDataException;
}