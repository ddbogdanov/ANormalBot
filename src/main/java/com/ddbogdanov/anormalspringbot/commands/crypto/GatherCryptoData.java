package com.ddbogdanov.anormalspringbot.commands.crypto;

import com.ddbogdanov.anormalspringbot.commands.common.CommandInterface;
import com.ddbogdanov.anormalspringbot.model.repos.CryptoRepo;
import com.ddbogdanov.anormalspringbot.tasks.FetchChartDataTask;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class GatherCryptoData extends Command implements CommandInterface<GatherCryptoData> {

    ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
    ArrayList<Future<?>> futures = new ArrayList<>();
    private EventWaiter waiter;

    @Autowired
    CryptoRepo cryptoRepo;

    @Autowired
    public GatherCryptoData(EventWaiter waiter) {
        super.name = "StartGatheringCryptoData, !gcd, !StopGatheringCryptoData, !stopgcd";
        super.help = "Start/Stop Gathering data on a cryptocurrency for charting";
        super.aliases = new String[]{"StartGatheringCryptoData", "startgcd", "StopGatheringCryptodata", "stopgcd"};
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

        //User input length of 1. ex. !startgcd \n{bot response}\n symbol
        if(request.length == 1) {
            if (request[0].equalsIgnoreCase("!StartGatheringCryptoData") || request[0].equalsIgnoreCase("!startgcd")) {
                event.reply("Sure, what symbol would you like to gather?");
                waiter.waitForEvent(GuildMessageReceivedEvent.class, e -> e.getAuthor().equals(event.getAuthor()) && e.getChannel().equals(event.getChannel()), e -> {
                    try {
                        String symbol = e.getMessage().getContentRaw().toUpperCase();
                        futures.add(s.scheduleWithFixedDelay(new FetchChartDataTask(cryptoRepo, symbol), 0, 10, TimeUnit.SECONDS));
                        event.reply("Ok, I'm gathering data for: " + symbol);

                    } catch (Exception ex) {
                        event.reply("Looks like the symbol you offered isn't a valid one.");
                    }

                }, 30, TimeUnit.SECONDS, () -> event.reply("You didn't provide a symbol! - Request timed out."));
            }
            else if (request[0].equalsIgnoreCase("!StopGatheringCryptoData") || request[0].equalsIgnoreCase("!stopgcd")) {
                event.reply("Ok, stopping!");
                for(Future<?> f : futures) {
                    f.cancel(true);
                }
            }
        }

        //User input length of 2. ex. !startgcd symbol
        else if(request.length == 2) {
            if(request[0].equalsIgnoreCase("!StartGatheringCryptoData") || request[0].equalsIgnoreCase("!startgcd")) {
                try {
                    String symbol = request[1].toUpperCase();
                    futures.add(s.scheduleWithFixedDelay(new FetchChartDataTask(cryptoRepo, symbol), 0, 10, TimeUnit.SECONDS));
                    event.reply("Ok, I'm gathering data for: " + symbol);
                } catch (Exception ex) {
                    event.getChannel().sendMessage("Looks like the symbol you offered isn't a valid one.").queue();
                    ex.printStackTrace();
                }

            }
            else if(request[0].equalsIgnoreCase("!StopGatheringCryptoData") || request[0].equalsIgnoreCase("!stopgcd")) {
                event.reply("Ok, stopping!");
                for(Future<?> f : futures) {
                    f.cancel(true);
                }
            }
        }
    }
}
