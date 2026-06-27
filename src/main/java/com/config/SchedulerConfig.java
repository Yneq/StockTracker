package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.service.StockService;

/**
 * 定時任務設定。
 * 這個檔案是從 Main.java 拆出來的，原本 @Scheduled 方法
 * 跟 main() 進入點寫在同一個檔案裡。拆開後 Main.java
 * 只負責啟動，定時任務的邏輯獨立管理，互不影響。
 *
 * @Component 讓 Spring Boot 啟動時自動把這個類別納入管理，
 * 不需要在 Main.java 裡手動註冊。
 */
@Component
public class SchedulerConfig {

    @Autowired
    private StockService stockService;

    // 🔄 每 5 秒自動執行的定時任務
    @Scheduled(fixedRate = 5000)
    public void autoFetchTask() {
        try {
            stockService.downloadAndStoreStockData();
        } catch (Exception e) {
            System.err.println("⚠️ 定時抓取發生嚴重異常！");
            e.printStackTrace();
        }
    }
}