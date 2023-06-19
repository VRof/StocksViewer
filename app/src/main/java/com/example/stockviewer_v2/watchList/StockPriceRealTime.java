package com.example.stockviewer_v2.watchList;

import java.io.Serializable;

public class StockPriceRealTime implements Serializable {
    private double close;
    private double price;
    private String symbol;

    private double dailyChange;

    public StockPriceRealTime(double dailyChange, double price, String symbol) {
        this.close = dailyChange;
        this.price = price;
        this.symbol = symbol;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setDailyChange(double dailyChange) {
        this.dailyChange = dailyChange;
    }

    public double getDailyChange() {
        return this.dailyChange;
    }
}
