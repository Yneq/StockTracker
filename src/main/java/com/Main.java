package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 應用程式進入點。
 *
 * 這個檔案只負責「啟動」這一件事，不寫任何 API 端點或業務邏輯。
 * 之前 API 邏輯（@GetMapping）跟定時任務（@Scheduled）都跟
 * main() 寫在同一個檔案，改 API 時容易不小心動到啟動程式本身，
 * 導致整個專案啟動失敗。
 *
 * 拆解後：
 * - API 端點 → com.controller.StockController
 * - 定時任務 → com.config.SchedulerConfig
 *
 * @SpringBootApplication 啟動時，Spring 會自動掃描同 package
 * 以及所有子 package（com.controller、com.config、com.service...）
 * 底下標註 @RestController / @Component / @Service 的類別並自動載入，
 * 不需要在這裡手動註冊任何東西。
 */
@SpringBootApplication
@EnableScheduling
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}