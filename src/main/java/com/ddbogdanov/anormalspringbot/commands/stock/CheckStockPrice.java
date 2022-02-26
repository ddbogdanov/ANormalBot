package com.ddbogdanov.anormalspringbot.commands.stock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.ddbogdanov.anormalspringbot.model.Stock;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
import java.util.concurrent.TimeUnit;

public class CheckStockPrice extends Command {

    private final EventWaiter waiter;

    public CheckStockPrice(EventWaiter waiter) {
        super.name = "checkstockprice, !csp";
        super.help = "Get current price of a stock by its symbol";
        super.aliases = new String[]{"checkstockprice", "csp"};
        super.cooldown = 2;
        this.waiter = waiter;
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] request = event.getMessage().getContentRaw().split("\\s+");
        Member member = event.getMessage().getMember();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        String date = LocalDate.now().format(dateFormat);
        String time = LocalTime.now().format(timeFormat);

        if(request.length == 1) {
            event.reply("Sure, what symbol would you like to check?");

            waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()), e -> {

                try {
                    String symbol = e.getMessage().getContentRaw().toUpperCase();
                    createEmbeddedMessage(event, member, date, time, symbol);
                } catch (Exception ex) {
                    event.reply("Looks like the symbol you offered isn't a valid one.");
                }

            }, 30, TimeUnit.SECONDS, () -> event.reply("You didn't provide a symbol! - Request timed out."));
        }
        else if(request.length == 2) {
            String symbol = request[1].toUpperCase();

            if(symbol.equals("UZI")) {
                event.reply("```Last price of " + symbol + " at:\n\n" + time + "\n\nis:\n\n" + "$852,742,223.43" + " USD\n\nThis symbol is showing massive growth over the past 3 days with an ROI of 654,205%```");
            }
            else {
                try {
                    if(member == null) {
                        event.reply("The member that sent the request doesnt exist. or something");
                        return;
                    }
                    createEmbeddedMessage(event, member, date, time, symbol);
                } catch (Exception ex) {
                    event.reply("Looks like the symbol you offered isn't a valid one.");
                }
            }
        }
        else if(request.length > 2) {
            event.reply("Sorry, but I couldn't recognize the command - proper format is !checkstock {symbol}");
        }
    }

    private Stock fetchAsset(String symbol) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_EVEN);

        double price;
        double yearHigh;
        double yearLow;

        try {
            final HttpUriRequest request = RequestBuilder
                    .get("https://sandbox.tradier.com/v1/markets/quotes")
                    .addHeader("API KEY NAME", "API KEY VALUE")
                    .addHeader("Accept", "application/json")
                    .addParameter("symbols", symbol)
                    .addParameter("greeks", "false")
                    .build();
            final HttpResponse response = HttpClientBuilder.create().build().execute(request);
            final String jsonString = EntityUtils.toString(response.getEntity());
            final JsonNode json = new ObjectMapper().readTree(jsonString);

            price = Double.parseDouble(json.get("quotes").get("quote").get("last").toString());

            yearHigh = Double.parseDouble(json.get("quotes").get("quote").get("week_52_high").toString());
            yearHigh = Double.parseDouble(df.format(yearHigh));

            yearLow = Double.parseDouble(json.get("quotes").get("quote").get("week_52_low").toString());
            yearLow = Double.parseDouble(df.format(yearLow));

            System.out.println("Requested price for - " + symbol + ": "  + price);
        }
        catch(Exception ex) {
            System.err.println("Quote for given symbol wasn't found");
            return null;
        }

        return new Stock(UUID.randomUUID(), symbol, price, yearHigh, yearLow);
    }
    private void createEmbeddedMessage(CommandEvent event, Member member, String date, String time, String symbol) {
        Stock asset = fetchAsset(symbol);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(11350545) //#1137AD, #2CB5E0, #E49367, #AD3211, #49557A
                .setThumbnail("https://effectlist.com/files/thumbnails/1372583921912664.png")
                .setAuthor("'" + member.getUser().getName() + "' Requested: ", "https://google.com/search?q=" + symbol, member.getUser().getAvatarUrl())
                .setTitle(symbol + " - Outlook")
                .setDescription("Here's a look at " + symbol + " for you.")
                .addField("Date ", date, true)
                .addField("Time: ", time, true)
                .addField("Price:",  "$"+ asset.getPrice() + " USD", false)
                .addField("52-Week High:", "$" + asset.getYearHigh(), true)
                .addField("52-Week Low:", "$" + asset.getYearLow(), true );
        event.reply(embedBuilder.build());
    }
}
