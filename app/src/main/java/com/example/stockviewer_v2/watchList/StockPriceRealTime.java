package com.example.stockviewer_v2.watchList;

public class StockPriceRealTime {
    private double dailyChange;
    private double price;
    private String symbol;

    public StockPriceRealTime(double dailyChange, double price, String symbol) {
        this.dailyChange = dailyChange;
        this.price = price;
        this.symbol = symbol;
    }

    public double getDailyChange() {
        return dailyChange;
    }

    public void setDailyChange(double dailyChange) {
        this.dailyChange = dailyChange;
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
}
