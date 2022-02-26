package com.ddbogdanov.anormalspringbot.model;

import java.util.UUID;

public class Stock {

    private UUID id;
    private String symbol;
    private double price;
    private double yearHigh;
    private double yearLow;

    public Stock() {
        id = UUID.randomUUID();
        symbol = null;
        price = 0.0;
    }
    public Stock(UUID id, String symbol, double price, double yearHigh, double yearLow) {
        this.id = id;
        this.symbol = symbol;
        this.price = price;
        this.yearHigh = yearHigh;
        this.yearLow = yearLow;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setYearHigh(double percentChange) {
        this.yearHigh = yearHigh;
    }

    public String getSymbol() {
        return symbol;
    }
    public double getPrice() {
        return price;
    }
    public double getYearHigh() {
        return yearHigh;
    }
    public double getYearLow() {
        return yearLow;
    }
}
