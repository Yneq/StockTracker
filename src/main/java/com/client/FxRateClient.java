package com.client;

import java.math.BigDecimal;

import com.util.HttpUtil;

public class FxRateClient {

    public BigDecimal getUsdTwdRate() {

        try {

            String html =
                    HttpUtil.fetchJson(
                            "https://rate.bot.com.tw/xrt?Lang=zh-TW");

            int usdIndex =
                    html.indexOf("USD");

            if (usdIndex != -1) {

                int targetIndex =
                        html.indexOf(
                                "data-table=\"本行即期賣出\"",
                                usdIndex);

                if (targetIndex != -1) {

                    int start =
                            html.indexOf(
                                    ">",
                                    targetIndex) + 1;

                    int end =
                            html.indexOf(
                                    "</td>",
                                    start);

                    return new BigDecimal(
                            html.substring(start, end)
                                    .trim());
                }
            }

        } catch (Exception e) {
        }

        return new BigDecimal("31.7");
    }
}