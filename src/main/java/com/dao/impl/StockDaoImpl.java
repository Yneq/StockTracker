package com.dao.impl;

import com.dao.StockDao;
import com.model.StockHistoryData;
import com.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDaoImpl implements StockDao {

    @Override
    public void insertBatch(List<StockHistoryData> stockList) throws Exception {
        String sql = "INSERT INTO stock_history (stock_code, stock_name, price, open_price, adr_price, arbitrage_space, trade_time) VALUES (?, ?, ?, ?, ?, ?, CAST(? AS TIME))";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (StockHistoryData stock : stockList) {
                pstmt.setString(1, stock.getStockCode());
                pstmt.setString(2, stock.getStockName());
                pstmt.setBigDecimal(3, stock.getPrice());
                pstmt.setBigDecimal(4, stock.getOpenPrice());
                pstmt.setBigDecimal(5, stock.getAdrPrice());
                pstmt.setBigDecimal(6, stock.getArbitrageSpace());
                pstmt.setString(7, stock.getTradeTime());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public List<StockHistoryData> getAllHistory() throws Exception {
        List<StockHistoryData> list = new ArrayList<>();
        String sql = "SELECT id, stock_code, stock_name, price, open_price, adr_price, arbitrage_space, trade_time, created_at FROM stock_history ORDER BY id DESC LIMIT 300";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                StockHistoryData data = new StockHistoryData(
                    rs.getInt("id"),
                    rs.getString("stock_code"),
                    rs.getString("stock_name"),
                    rs.getBigDecimal("price"),
                    rs.getBigDecimal("open_price"),
                    rs.getBigDecimal("adr_price"),
                    rs.getBigDecimal("arbitrage_space"),
                    rs.getTime("trade_time") != null ? rs.getTime("trade_time").toString() : "13:30:00",
                    rs.getTimestamp("created_at").toString()
                );
                list.add(data);
            }
        }
        return list;
    }
}