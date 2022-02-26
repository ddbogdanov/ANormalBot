package com.ddbogdanov.anormalspringbot.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="crypto_table")
public class Crypto {

    @Id
    @Type(type="org.hibernate.type.UUIDCharType")
    @Column(name="id")
    private UUID id;
    @Column(name="symbol")
    private String symbol;
    @Column(name="datetime")
    private String datetime;
    @Column(name="percent_change_day")
    private double percentChangeDay;
    @Column(name="percent_change_week")
    private double percentChangeWeek;
    @Column(name="price")
    private String price;

    public Crypto() {
        id = UUID.randomUUID();
        symbol = null;
        datetime = null;
        percentChangeDay = 0.0;
        percentChangeWeek = 0.0;
        price = null;
    }
    public Crypto(UUID id, String symbol, String datetime, double percentChangeDay, double percentChangeWeek, String price) {
        this.id = id;
        this.symbol = symbol;
        this.datetime = datetime;
        this.percentChangeDay = percentChangeDay;
        this.percentChangeWeek = percentChangeWeek;
        this.price = price;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public void setPercentChange(double percentChangeDay) {
        this.percentChangeDay = percentChangeDay;
    }
    public void setPercentChangeDay(double percentChangeDay) {
        this.percentChangeDay = percentChangeDay;
    }
    public void setPercentChangeWeek(double percentChangeWeek) {
        this.percentChangeWeek = percentChangeWeek;
    }

    public UUID getId() {
        return id;
    }
    public String getSymbol() {
        return symbol;
    }
    public String getDatetime() {
        return datetime;
    }
    public double getPercentChangeDay() {
        return percentChangeDay;
    }
    public double getPercentChangeWeek() { return percentChangeWeek; }
    public String getPrice() {
        return price;
    }

}