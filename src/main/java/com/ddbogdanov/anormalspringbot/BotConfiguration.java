package com.ddbogdanov.anormalspringbot;

import com.ddbogdanov.anormalspringbot.commands.common.CommandInterface;
import com.ddbogdanov.anormalspringbot.commands.stock.CheckStockPrice;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class BotConfiguration {

    @Value("${token}")
    private String token;

    @Bean
    public <T extends Command> JDA jda(List<CommandInterface<T>> cmds) throws Exception {
        JDA jda = JDABuilder.createDefault(token).build();

        EventWaiter waiter = new EventWaiter();
        CommandClientBuilder builder = new CommandClientBuilder();

        builder.setOwnerId(jda.getSelfUser().getId());
        builder.setPrefix("!");
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("Attaining Forbes 500"));

        builder.addCommands(
                new CheckStockPrice(waiter) //TODO: REFACTOR CheckStockPrice AND REMOVE
        );

        for(CommandInterface<T> cmd : cmds ) { // Loop through each implementation of CommandInterface
            cmd.setWaiter(waiter); // Set EventWaiter for each CommandInterface
            builder.addCommand((Command)cmd); //Add the command to our CommandClientBuilder, thus creating a spring-managed command
        }
        CommandClient client = builder.build();

        jda.addEventListener(client);
        jda.addEventListener(waiter);

        return jda;
    }

    @Bean
    protected EventWaiter ew() {
        return new EventWaiter();
    }
}
