package com.controller;

import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dao.UserDao;
import com.dao.WatchlistDao;
import com.dao.impl.UserDaoImpl;
import com.dao.impl.WatchlistDaoImpl;
import com.model.User;
import com.model.Watchlist;
import com.model.watchlist.AddWatchlistRequest;
import com.util.JwtUtil;
import com.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private WatchlistDao watchlistDao = new WatchlistDaoImpl();
    private UserDao userDao = new UserDaoImpl();
    private final Gson gson = new Gson();

    // token環境變數讀取

    // 共用：從 token 解析出登入者的 userId
    private Long getUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = JwtUtil.getUsername(token);
        User user = userDao.findByUsername(username);
        return user.getId();
    }

    // 驗證股票代碼是否真實存在，並回傳市場類別 ("twse"=上市 / "tpex"=上櫃)
    // 回傳 null 表示查不到（不存在）
    private String checkMarketType(String stockCode) {
        try {
            String url = "https://api.finmindtrade.com/api/v4/data?dataset=TaiwanStockInfo" +
                    "&data_id=" + stockCode +
                    "&token=" + URLEncoder.encode(com.util.FinMindConfig.getToken(), StandardCharsets.UTF_8);

            String json = HttpUtil.fetchJson(url);
            JsonObject root = gson.fromJson(json, JsonObject.class);
            JsonArray data = root.getAsJsonArray("data");

            if (data == null || data.size() == 0) {
                return null;
            }

            JsonObject first = data.get(0).getAsJsonObject();
            // type 欄位值通常是 "twse" 或 "tpex"
            String type = first.has("type") ? first.get("type").getAsString() : "twse";
            return type;

        } catch (Exception e) {
            System.out.println("驗證股票代碼失敗: " + stockCode + " -> " + e.getMessage());
            return null;
        }
    }

    // GET /api/watchlist
    @GetMapping
    public List<Watchlist> getWatchlist(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserId(authHeader);
        return watchlistDao.findByUserId(userId);
    }

    // POST /api/watchlist
    @PostMapping
    public ResponseEntity<?> addStock(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddWatchlistRequest req) {

        String stockCode = req.getStockCode();

        // 驗證：股票代碼必須是 4 位數字（台股代碼格式）
        if (stockCode == null || !stockCode.matches("\\d{4}")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "股票代碼格式錯誤，請輸入4位數字（例如 2330）"));
        }

        // 驗證：股票代碼必須真實存在，並取得市場類別（上市/上櫃）
        String market = checkMarketType(stockCode);
        if (market == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "找不到此股票代碼，請確認輸入是否正確"));
        }

        Long userId = getUserId(authHeader);
        boolean success = watchlistDao.addStock(userId, stockCode, market);

        if (!success) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "已經在追蹤清單中"));
        }

        return ResponseEntity.ok(Map.of("message", "已加入追蹤清單", "market", market));
    }

    // DELETE /api/watchlist/{stockCode}
    @DeleteMapping("/{stockCode}")
    public ResponseEntity<?> deleteStock(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String stockCode) {

        Long userId = getUserId(authHeader);
        watchlistDao.deleteStock(userId, stockCode);

        return ResponseEntity.ok(Map.of("message", "已移除"));
    }
}