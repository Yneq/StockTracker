package com.model;

import java.math.BigDecimal;

public class StockInfo {

    public String c; // 股票代號
    public String n; // 公司簡稱

    public String z; // 成交價
    public String b; // 買價
    public String a; // 賣價

    public String o; // 開盤價
    public String y; // 昨收價

    public String t; // 成交時間

    private boolean isNumeric(String str) {

        if (str == null || str.trim().isEmpty()) {
            return false;
        }

        return str.trim()
                  .matches("^[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)$");
    }

    /**
     * 成交價
     * ↓
     * 買一
     * ↓
     * 賣一
     * ↓
     * 昨收
     */
    public BigDecimal getPriceAsBigDecimal() {

        if (isNumeric(z)) {
            return new BigDecimal(z.trim());
        }

        if (b != null && !b.trim().isEmpty()) {

            String[] bidArray = b.split("_");

            if (bidArray.length > 0 &&
                isNumeric(bidArray[0])) {

                return new BigDecimal(
                        bidArray[0].trim());
            }
        }

        if (a != null && !a.trim().isEmpty()) {

            String[] askArray = a.split("_");

            if (askArray.length > 0 &&
                isNumeric(askArray[0])) {

                return new BigDecimal(
                        askArray[0].trim());
            }
        }

        // 最後防線
        if (isNumeric(y)) {
            return new BigDecimal(y.trim());
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getOpenPriceAsBigDecimal() {

        if (isNumeric(o)) {
            return new BigDecimal(o.trim());
        }

        return getPriceAsBigDecimal();
    }

    public BigDecimal getYesterdayCloseAsBigDecimal() {

        if (isNumeric(y)) {
            return new BigDecimal(y.trim());
        }

        return BigDecimal.ZERO;
    }
}