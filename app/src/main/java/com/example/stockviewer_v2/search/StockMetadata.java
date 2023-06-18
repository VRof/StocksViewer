package com.example.stockviewer_v2.search;

public class StockMetadata {
    private String symbol;
    private String name;
    private String currency;
    private String exchange;
    private String micCode;
    private String country;
    private String type;

    public StockMetadata(String symbol, String name, String currency, String exchange, String micCode,
                         String country, String type) {
        this.symbol = symbol;
        this.name = name;
        this.currency = currency;
        this.exchange = exchange;
        this.micCode = micCode;
        this.country = country;
        this.type = type;
    }

    // Getter methods for each field

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public String getExchange() {
        return exchange;
    }

    public String getMicCode() {
        return micCode;
    }

    public String getCountry() {
        return country;
    }

    public String getType() {
        return type;
    }
    @Override
    public String toString() {
        return symbol;
    }
}
