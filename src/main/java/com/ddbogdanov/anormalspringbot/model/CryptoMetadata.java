package com.ddbogdanov.anormalspringbot.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="crypto_metadata")
public class CryptoMetadata {

    @Id
    @Type(type="org.hibernate.type.UUIDCharType")
    @Column(name="id")
    private UUID id;
    @Column(name="symbol")
    private String symbol;
    @Column(name="datetime")
    private String datetime;
    @Column(name="website_url")
    private String websiteUrl;
    @Column(name="logo_url")
    private String logoUrl;

    public CryptoMetadata() {
        id = UUID.randomUUID();
        symbol = null;
        datetime = null;
        websiteUrl = null;
        logoUrl = null;
    }
    public CryptoMetadata(UUID id, String symbol, String date, String websiteUrl, String logoUrl) {
        this.id = id;
        this.symbol = symbol;
        this.datetime = date;
        this.websiteUrl = websiteUrl;
        this.logoUrl = logoUrl;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public UUID getId() {
        return id;
    }
    public String getSymbol() {
        return symbol;
    }
    public String getWebsiteUrl() {
        return websiteUrl;
    }
    public String getLogoUrl() {
        return logoUrl;
    }
    public String getDatetime() {
        return datetime;
    }
}
