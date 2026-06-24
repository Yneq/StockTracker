package com.client;

import com.model.Watchlist;
import java.util.List;
import java.util.stream.Collectors;

public class TwseClient {

    // 預設基礎股票（即使 watchlist 是空的，主頁三檔還是要能顯示），全部都是上市股
    private static final List<String> DEFAULT_CODES =
        List.of("2330", "2317", "2454");

    private static final String BASE_URL =
        "https://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=";

    // 舊版相容：只抓固定三檔（基礎股）
    public String getStockJson() {
        try {
            String exCh = DEFAULT_CODES.stream()
                .map(code -> "tse_" + code + ".tw")
                .collect(Collectors.joining("%7C"));
            return com.util.HttpUtil.fetchJson(BASE_URL + exCh);
        } catch (Exception e) {
            return "";
        }
    }

    // 新版：依傳入的 watchlist 股票（含市場類別）動態組網址，並合併基礎三檔
    public String getStockJson(List<Watchlist> watchedStocks) {
        try {
            StringBuilder exChBuilder = new StringBuilder();

            // 基礎三檔，固定是上市
            for (String code : DEFAULT_CODES) {
                appendCode(exChBuilder, code, "twse");
            }

            // 使用者追蹤的股票，依各自的 market 決定前綴
            for (Watchlist w : watchedStocks) {
                // 避免跟基礎三檔重複
                if (DEFAULT_CODES.contains(w.getStockCode())) continue;
                appendCode(exChBuilder, w.getStockCode(), w.getMarket());
            }

            String url = BASE_URL + exChBuilder.toString();
            return com.util.HttpUtil.fetchJson(url);

        } catch (Exception e) {
            return "";
        }
    }

    private void appendCode(StringBuilder builder, String code, String market) {
        // FinMind 的 type 欄位是 "twse"(上市) 或 "tpex"(上櫃)
        // TWSE 即時行情 API 的前綴是 tse_ (上市) 或 otc_ (上櫃)
        String prefix = "tpex".equalsIgnoreCase(market) ? "otc_" : "tse_";

        if (builder.length() > 0) {
            builder.append("%7C");
        }
        builder.append(prefix).append(code).append(".tw");
    }
}