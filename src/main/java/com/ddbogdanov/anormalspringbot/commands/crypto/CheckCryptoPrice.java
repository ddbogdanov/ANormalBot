package com.ddbogdanov.anormalspringbot.commands.crypto;

import com.ddbogdanov.anormalspringbot.commands.common.CommandInterface;
import com.ddbogdanov.anormalspringbot.model.Crypto;
import com.ddbogdanov.anormalspringbot.model.CryptoMetadata;
import com.ddbogdanov.anormalspringbot.model.repos.CryptoMetadataRepo;
import com.ddbogdanov.anormalspringbot.model.repos.CryptoRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CheckCryptoPrice extends Command implements CommandInterface<CheckCryptoPrice> {

    private EventWaiter waiter;

    @Autowired
    CryptoRepo cryptoRepo;
    @Autowired
    CryptoMetadataRepo cryptoMetadataRepo;

    @Autowired
    public CheckCryptoPrice(EventWaiter waiter) {
        super.name = "checkcryptoprice, !ccp";
        super.help = "Get current price of a crypto by its symbol";
        super.aliases = new String[]{"checkcryptoprice", "ccp"};
        super.cooldown = 2;
        this.waiter = waiter;
    }
    @Override
    public void setWaiter(EventWaiter waiter) {
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {

        String[] request = event.getMessage().getContentRaw().split("\\s+");
        Member member = event.getMessage().getMember();

        //User input of length 1. ex. !ccp \n{bot response}\n symbol
        if(request.length == 1) {
            event.reply("Sure, what symbol would you like to check?");

            waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()), e -> {
                try {
                    String symbol = e.getMessage().getContentRaw().toUpperCase();

                    createEmbeddedMessage(event, member, getInstantDate(), getInstantTime(), symbol);

                } catch (Exception ex) {
                    event.reply("Looks like the symbol you offered isn't a valid one.");
                }

            }, 30, TimeUnit.SECONDS, () -> event.reply("You didn't provide a symbol! - Request timed out."));
        }

        //User input of length 2 ex. !ccp symbol
        else if(request.length == 2) {
            String symbol = request[1].toUpperCase();
            try {
                createEmbeddedMessage(event, member, getInstantDate(), getInstantTime(), symbol);
            } catch(Exception ex) {
                event.reply("Looks like the symbol you offered isn't a valid one.");
            }
        }
        else if(request.length > 2){
            event.reply("Sorry, but I couldn't recognize the command - proper format is !checkcryptoprice {symbol}");
        }
    }

    private Crypto fetchAsset(String symbol) throws Exception { //TODO Extract into a util package
        DecimalFormat df = (DecimalFormat)DecimalFormat.getNumberInstance(Locale.US);
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

        System.out.println("Requested price for - " + symbol + ": "  + price);

        return new Crypto(UUID.randomUUID(), symbol, getInstantDate() + " " + getInstantTime(), percentChangeDay, percentChangeWeek, price);
    }
    private CryptoMetadata fetchMetadata(String symbol) throws Exception {
        String websiteUrl, logoUrl;

        final HttpUriRequest metadataRequest = RequestBuilder
                .get("https://pro-api.coinmarketcap.com/v1/cryptocurrency/info")
                .addHeader("API KEY NAME", "API KEY VALUE")
                .addParameter("symbol", symbol)
                .addParameter("aux", "urls,logo")
                .build();
        final HttpResponse metadataResponse = HttpClientBuilder.create().build().execute(metadataRequest);
        final String metadataJsonString = EntityUtils.toString(metadataResponse.getEntity());
        final JsonNode metadataJson = new ObjectMapper().readTree(metadataJsonString);

        websiteUrl = metadataJson.get("data").get(symbol).get("urls").get("website").toString();
        websiteUrl = websiteUrl.replace("[", "");
        websiteUrl = websiteUrl.replace("]", "");
        websiteUrl = websiteUrl.replace("\"", "");

        logoUrl = metadataJson.get("data").get(symbol).get("logo").toString();
        logoUrl = logoUrl.replace("\"", "");

        System.out.println("Requested metadata for - " + symbol);

        return new CryptoMetadata(UUID.randomUUID(), symbol, getInstantDate() + " " + getInstantTime(), websiteUrl, logoUrl);
    }

    private void createEmbeddedMessage(CommandEvent event, Member member, String date, String time, String symbol) {
        Crypto asset;
        CryptoMetadata assetMetadata;

        if(cryptoMetadataRepo.existsBySymbol(symbol)) {
            try {
                asset = fetchAsset(symbol);
                cryptoRepo.save(asset); //Save asset for charting whether or not metadata exists

                assetMetadata = cryptoMetadataRepo.findBySymbol(symbol).get(0);
            } catch(Exception ex) {
                event.reply("It seems like the symbol you provided is invalid.");
                ex.printStackTrace();
                return;
            }
        }
        else {
            try {
                asset = fetchAsset(symbol);
                assetMetadata = fetchMetadata(symbol);

                cryptoRepo.save(asset); //Save asset for charting whether or not metadata exists
                cryptoMetadataRepo.save(assetMetadata);
            } catch(Exception ex) {
                event.reply("It seems like the symbol you provided is invalid.");
                ex.printStackTrace();
                return;
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(2930144) //#1137AD, #2CB5E0, #E49367, #AD3211, #49557A
                .setThumbnail(assetMetadata.getLogoUrl())
                .setAuthor("'" + member.getUser().getName() + "' Requested: ", assetMetadata.getWebsiteUrl(), member.getUser().getAvatarUrl())
                .setTitle(symbol + " - Outlook")
                .setDescription("Here's a look at " + symbol + " for you.")
                .addField("Date ", date, true)
                .addField("Time: ", time, true)
                .addField("Price:",  "$"+ asset.getPrice() + " USD", false)
                .addField("Day Change:", asset.getPercentChangeDay() + "%", true)
                .addField("Week Change", asset.getPercentChangeWeek() + "%", true);
        event.reply(embedBuilder.build());
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