package com.ddbogdanov.anormalspringbot.commands.misc;

import com.ddbogdanov.anormalspringbot.commands.common.CommandInterface;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Ping extends Command implements CommandInterface<Ping> {

    @Autowired
    public Ping() {
        super.name = "ping";
        super.help = "Ping the bot and check its latency";
        super.aliases = new String[]{"ping"};
        super.cooldown = 0;
    }

    @Override
    protected void execute(CommandEvent event) {
        long time = System.currentTimeMillis();

        event.getChannel().sendMessage("Pong!").queue(response -> {
            response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
        });
    }
}
