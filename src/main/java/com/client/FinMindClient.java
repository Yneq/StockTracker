package com.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.model.finmind.MonthlyRevenue;

import com.util.HttpUtil;
import java.util.ArrayList;
import java.util.List;

import com.model.finmind.RevenuePoint;

public class FinMindClient {

    // token 改從環境變數讀取，不寫死在程式碼裡（FinMindConfig.getToken()）

    private final Gson gson = new Gson();
    public MonthlyRevenue getLatestRevenue(
            String stockNo) {

        try {

        	String json =
        	        getMonthlyRevenue(stockNo);

        	System.out.println("===== FINMIND =====");
        	System.out.println(json);

            JsonObject root =
                    gson.fromJson(
                            json,
                            JsonObject.class);

            JsonArray data =
                    root.getAsJsonArray("data");

            if (data == null || data.size() == 0) {
                return null;
            }

            JsonObject latest =
                    data.get(data.size() - 1)
                        .getAsJsonObject();

            return new MonthlyRevenue(
                    latest.get("date")
                          .getAsString(),
                    latest.get("stock_id")
                          .getAsString(),
                    latest.get("revenue")
                          .getAsBigDecimal());

        } catch (Exception e) {
        	System.out.println("FINMIND ERROR");

            e.printStackTrace();

            return null;
            

            
        }
    }

    public String getMonthlyRevenue(String stockNo) {

        try {

            String url =
                    "https://api.finmindtrade.com/api/v4/data?"
                    + "dataset=TaiwanStockMonthRevenue"
                    + "&data_id="
                    + stockNo
                    + "&start_date=2024-01-01"
                    + "&token="
                    + URLEncoder.encode(
                            com.util.FinMindConfig.getToken(),
                            StandardCharsets.UTF_8);
            
            System.out.println(url);
            
            return HttpUtil.fetchJson(url);

        } catch (Exception e) {
            return "";
        }
    }
    public List<RevenuePoint> getRevenueHistory(
            String stockNo) {

        List<RevenuePoint> result =
                new ArrayList<>();

        try {

            String json =
                    getMonthlyRevenue(stockNo);

            JsonObject root =
                    gson.fromJson(
                            json,
                            JsonObject.class);

            JsonArray data =
                    root.getAsJsonArray("data");

            if (data == null) {
                return result;
            }

            for (int i = 0; i < data.size(); i++) {

                JsonObject item =
                        data.get(i)
                            .getAsJsonObject();

                result.add(
                        new RevenuePoint(
                                item.get("date")
                                    .getAsString(),
                                item.get("revenue")
                                    .getAsBigDecimal()));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }
    
    

}