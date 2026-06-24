package com.util;

public class FinMindConfig {

    // 從環境變數 FINMIND_TOKEN 讀取，不寫死在程式碼裡，避免上傳到 GitHub 洩漏
    public static String getToken() {
        String token = System.getenv("FINMIND_TOKEN");

        if (token == null || token.isEmpty()) {
            throw new RuntimeException(
                "找不到環境變數 FINMIND_TOKEN，請先設定後再啟動程式");
        }

        return token;
    }
}