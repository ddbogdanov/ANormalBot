package com.ddbogdanov.anormalspringbot.tasks;

import com.ddbogdanov.anormalspringbot.model.Crypto;
import com.ddbogdanov.anormalspringbot.model.repos.CryptoRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FetchChartDataTask implements Runnable {

    final CryptoRepo cryptoRepo;
    private final String symbol;

    public FetchChartDataTask(CryptoRepo cryptoRepo, String symbol) {
        this.cryptoRepo = cryptoRepo;
        this.symbol = symbol;
    }

    @Override
    public void run() {
        System.out.println("Saving!");
        try {
            cryptoRepo.save(fetchAsset(symbol));
        }
        catch(Exception ex) {
            //tell user symbol doesnt exist
            ex.printStackTrace();
        }
    }

    private Crypto fetchAsset(String symbol) throws Exception { //TODO Extract into a util package
        DecimalFormat df = new DecimalFormat("###,###.########");
        df.setRoundingMode(RoundingMode.HALF_EVEN);

        double percentChangeDay;
        double percentChangeWeek;
        double tempPrice;
        String price;

        //Request quote information
        final HttpUriRequest quoteRequest = RequestBuilder
                .get("https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest")
                .addHeader("API KEY NAME", "API KEY VALUE")
                .addParameter("symbol", symbol)
                .build();
        final HttpResponse quoteResponse = HttpClientBuilder.create().build().execute(quoteRequest);
        final String quoteJsonString = EntityUtils.toString(quoteResponse.getEntity());
        final JsonNode quoteJson = new ObjectMapper().readTree(quoteJsonString);

        tempPrice = Double.parseDouble(quoteJson.get("data").get(symbol).get("quote").get("USD").get("price").toString());
        price = df.format(tempPrice);

        percentChangeDay = Double.parseDouble(quoteJson.get("data").get(symbol).get("quote").get("USD").get("percent_change_24h").toString());
        percentChangeDay = Double.parseDouble(df.format(percentChangeDay));

        percentChangeWeek = Double.parseDouble(quoteJson.get("data").get(symbol).get("quote").get("USD").get("percent_change_7d").toString());
        percentChangeWeek = Double.parseDouble(df.format(percentChangeWeek));

        System.out.println("Requested price for - " + symbol + ": " + price);

        return new Crypto(UUID.randomUUID(), symbol, getInstantDate() + " " + getInstantTime(), percentChangeDay, percentChangeWeek, price);
    }

    private String getInstantDate() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return LocalDate.now().format(dateFormat);
    }
    private String getInstantTime() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.now().format(timeFormat);
    }
}
