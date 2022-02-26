package com.ddbogdanov.anormalspringbot.commands.crypto;

import com.ddbogdanov.anormalspringbot.commands.common.CommandInterface;
import com.ddbogdanov.anormalspringbot.model.Crypto;
import com.ddbogdanov.anormalspringbot.model.repos.CryptoMetadataRepo;
import com.ddbogdanov.anormalspringbot.model.repos.CryptoRepo;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class CryptoChart extends Command implements CommandInterface<CryptoChart> {

    //TODO Allow user to select a date range e.g 1h, 1d, 7d, etc.
    private EventWaiter waiter;

    @Autowired
    CryptoRepo cryptoRepo;
    @Autowired
    CryptoMetadataRepo cryptoMetadataRepo;

    @Autowired
    public CryptoChart(EventWaiter waiter) {
        super.name = "cryptochart, !cc";
        super.help = "Get a line chart of a cryptocurrency";
        super.aliases = new String[]{"cryptochart", "cc"};
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

        if(request.length == 1) {
            event.reply("Sure, what symbol would you like to see a chart for?");
            waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()), e -> {
                try {
                    String symbol = e.getMessage().getContentRaw().toUpperCase();
                    createChart(event, symbol);

                } catch (Exception ex) {
                    event.reply("Looks like the symbol you offered isn't a valid one.");
                }

            }, 30, TimeUnit.SECONDS, () -> event.reply("You didn't provide a symbol! - Request timed out."));
        }
        else if(request.length == 2) {
            try {
                String symbol = request[1].toUpperCase();
                createChart(event, symbol);
            }
            catch(Exception ex) {
                event.reply("Looks like the symbol you offered isn't a valid one.");
            }
        }
    }

    private XYDataset createDataset(String symbol) {
        final TimePeriodValues series = new TimePeriodValues( "Crypto Data" );
        List<Crypto> cryptoList = cryptoRepo.findBySymbolOrderByDatetimeAsc(symbol);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date;

        for(Crypto c : cryptoList) {
            try {
                date = sdf.parse(c.getDatetime());
                series.add(new Second(date), Double.parseDouble(c.getPrice())); //TODO parse anything with a comma as formatting with DecimalFormatter
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        XYDataset dataset = new TimePeriodValuesCollection(series);
        System.out.println(cryptoList.size());

        return dataset;
    }
    private void createChart(CommandEvent event, String symbol) {
        JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
                null,
                "Date/Time",
                "Price",
                createDataset(symbol),
                false, false, false);

        lineChart.setAntiAlias(true);
        lineChart.setBackgroundPaint(new Color(3816514));

        XYPlot plot = lineChart.getXYPlot();

        DateAxis dateAxis = (DateAxis)plot.getDomainAxis();
        dateAxis.setDateFormatOverride(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"));
        dateAxis.setVerticalTickLabels(true);

        plot.getDomainAxis().setVisible(true);
        plot.getDomainAxis().setTickLabelsVisible(true);
        plot.getDomainAxis().setLabelPaint(Color.WHITE);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.HOUR, 1));

        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getRangeAxis().setTickMarkPaint(Color.WHITE);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
        plot.getRenderer().setSeriesPaint(0, new Color(2930144));

        plot.getDomainAxis().setLowerMargin(.0);
        plot.getDomainAxis().setUpperMargin(.0);
        plot.getRangeAxis().setLowerMargin(.1);
        plot.getRangeAxis().setUpperMargin(.1);
        plot.getRangeAxis().setAutoRange(true);
        ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false);
        ((NumberAxis)plot.getRangeAxis()).setNumberFormatOverride(NumberFormat.getInstance(Locale.US));

        plot.setBackgroundPaint(new Color(3816514));
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);

        File chartFile = new File("LineChart.png");
        try {
            ChartUtilities.saveChartAsPNG(chartFile, lineChart, 700, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(chartFile.getPath());

        createEmbeddedMessage(event, chartFile, symbol);
    }
    private void createEmbeddedMessage(CommandEvent event, File chartFile, String symbol) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(2930144)
                .setTitle("{Time Value} Chart - " + symbol)
                .setThumbnail(cryptoMetadataRepo.findBySymbol(symbol).get(0).getLogoUrl())
                .setImage("attachment://LineChart.png");

        event.getChannel().sendFile(chartFile, "LineChart.png").embed(builder.build()).queue();
    }
}