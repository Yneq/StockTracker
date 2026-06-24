package com.client;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.util.HttpUtil;

public class AdrClient {

    private static final String YAHOO_URL =
            "https://query1.finance.yahoo.com/v8/finance/chart/TSM";

    private final Gson gson = new Gson();

    public BigDecimal getTsmEquivalentPrice(
            BigDecimal usdTwdRate) {

        try {

            String yahooJson =
                    HttpUtil.fetchJson(YAHOO_URL);

            JsonObject jsonObject =
                    gson.fromJson(
                            yahooJson,
                            JsonObject.class);

            JsonArray result =
                    jsonObject
                            .getAsJsonObject("chart")
                            .getAsJsonArray("result");

            JsonObject meta =
                    result.get(0)
                          .getAsJsonObject()
                          .getAsJsonObject("meta");

            BigDecimal adrPrice =
                    meta.get("regularMarketPrice")
                            .getAsBigDecimal();

            return adrPrice
                    .multiply(usdTwdRate)
                    .divide(
                            new BigDecimal("5"),
                            2,
                            RoundingMode.HALF_UP);

        } catch (Exception e) {

            return BigDecimal.ZERO;
        }
    }
}