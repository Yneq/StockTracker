package com.model.ws;

public class StockUpdateMessage {

    private String stockCode;

    private String price;

    private String adrPremium;

    public StockUpdateMessage(
            String stockCode,
            String price,
            String adrPremium) {

        this.stockCode = stockCode;
        this.price = price;
        this.adrPremium = adrPremium;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getPrice() {
        return price;
    }

    public String getAdrPremium() {
        return adrPremium;
    }
}