package com.service.impl;

import com.dao.StockDao;
import com.dao.WatchlistDao;
import com.dao.impl.StockDaoImpl;
import com.dao.impl.WatchlistDaoImpl;
import com.exception.StockDataException;
import com.model.Watchlist;
import com.model.StockHistoryData;
import com.service.StockService;
import com.util.HttpUtil;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.service.analysis.AdrAnalysisService;
import com.model.analysis.AdrAnalysisResult;
import com.StockInfo;
import com.client.AdrClient;
import com.client.FxRateClient;
import com.client.TwseClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.model.ws.StockUpdateMessage;

@Service

public class StockServiceImpl implements StockService {
	
    private final StockDao stockDao = new StockDaoImpl();
    private final WatchlistDao watchlistDao = new WatchlistDaoImpl();
    
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

    private final TwseClient twseClient =new TwseClient();
    
    private final AdrClient adrClient =new AdrClient();
    private final FxRateClient fxRateClient =new FxRateClient();
    
    private final Gson gson = new Gson();
    private final AdrAnalysisService adrAnalysisService =new AdrAnalysisService();
    

    private static class StockResponse {
        List<StockInfo> msgArray;
    }

  

    @Override
    public void downloadAndStoreStockData() throws StockDataException {
        try {
        	// 查出所有使用者目前追蹤的股票（含市場類別），跟基礎三檔合併後一起抓
        	List<Watchlist> watchedStocks = watchlistDao.findAllDistinctStocks();
        	String twseJson = twseClient.getStockJson(watchedStocks);
            StockResponse stockResponse = gson.fromJson(twseJson, StockResponse.class);

            if (stockResponse == null || stockResponse.msgArray == null || stockResponse.msgArray.isEmpty()) return;

            // 抓取真實美股與台銀即時匯率
            BigDecimal usdRate =fxRateClient.getUsdTwdRate();

            BigDecimal realAdrPrice =adrClient.getTsmEquivalentPrice(
            		usdRate);
            List<StockHistoryData> batchList = new ArrayList<>();

            for (StockInfo info : stockResponse.msgArray) {
                // 防護：代碼或名稱缺失就跳過這一筆，避免整批 batch insert 被拖垮
                if (info.c == null || info.c.trim().isEmpty()) {
                    System.out.println("⚠️ 跳過：股票代碼為空");
                    continue;
                }
                if (info.n == null || info.n.trim().isEmpty()) {
                    System.out.println("⚠️ 跳過：股票代碼 " + info.c + " 沒有對應名稱（可能是錯誤或下市代碼）");
                    continue;
                }

                // 必須準確抓取市價與開盤價，儀表板才不會空
                BigDecimal twPrice = info.getPriceAsBigDecimal();
                BigDecimal openPrice = info.getOpenPriceAsBigDecimal();
                String time = info.t != null ? info.t : "13:30:00";
                
                BigDecimal currentAdr = BigDecimal.ZERO;
                BigDecimal netArbitrageSpace = BigDecimal.ZERO;

                // 只有台積電需要單獨進行跨國量化套利流分析
                if ("2330".equals(info.c)) {
                    currentAdr = realAdrPrice; 
                    
                    if (currentAdr.compareTo(BigDecimal.ZERO) > 0 && twPrice.compareTo(BigDecimal.ZERO) > 0) {
                        // 算出原始總溢價率 (Raw Spread)
                    	AdrAnalysisResult analysis =
                    	        adrAnalysisService.analyze(
                    	                twPrice,
                    	                currentAdr);

                    	netArbitrageSpace =
                    	        analysis.getNetSpread();
                        
                    	System.out.println(
                    	        "====== ADR Premium Analysis ======");

                    	System.out.println(
                    	        "Raw Spread: "
                    	        + analysis.getRawSpread()
                    	        + "%");

                    	System.out.println(
                    	        "Total Cost: "
                    	        + analysis.getTotalCost()
                    	        + "%");

                    	System.out.println(
                    	        "Net Spread: "
                    	        + analysis.getNetSpread()
                    	        + "%");

                    	System.out.println(
                    	        "==================================");
                        System.out.println("=========================================");
                    }
                }

                // 印出 Log，確保每一檔股票的市價流向清晰可見
                System.out.println(String.format("🕵️ 數據流入 -> %s | 市價: %s | 開盤: %s | 真實ADR折合: %s | 淨套利空間: %s%%", 
                        info.n, twPrice, openPrice, currentAdr, netArbitrageSpace));
                messagingTemplate.convertAndSend(
                        "/topic/stock",
                        new StockUpdateMessage(
                                info.c,
                                twPrice.toString(),
                                netArbitrageSpace.toString()));
                

                // 修正：將正確的 twPrice 與 openPrice 塞進每一個物件，丟進資料庫
                batchList.add(new StockHistoryData(0, info.c, info.n, twPrice, openPrice, currentAdr, netArbitrageSpace, time, ""));
            }

            stockDao.insertBatch(batchList);
            System.out.println("  💾 [Database] 歷史資料(含美股指標)寫入完畢！");

        } catch (Exception e) {
            System.err.println("❌ 抓取或寫入發生異常: " + e.getMessage());
            e.printStackTrace();
            throw new StockDataException("資料抓取與儲存失敗", e);
        }
    }

    @Override
    public List<StockHistoryData> fetchDisplayData() throws StockDataException {
        try { return stockDao.getAllHistory(); } catch (Exception e) { throw new StockDataException("讀取歷史資料失敗", e); }
    }

}