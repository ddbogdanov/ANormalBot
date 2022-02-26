package com.ddbogdanov.anormalspringbot.commands.crypto;

import com.ddbogdanov.anormalspringbot.commands.common.CommandInterface;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CryptoWatchlist extends Command implements CommandInterface<CryptoWatchlist> {

    private EventWaiter waiter;

    @Autowired
    public CryptoWatchlist(EventWaiter waiter) {
        super.name = "cryptowatchlist, !cwl";
        super.help = "Typical crypto watchlist stuff";
        super.aliases = new String[]{"cryptowatchlist", "cwl"};
        super.cooldown = 2;
        this.waiter = waiter;
    }

    @Override
    public void setWaiter(EventWaiter waiter) {
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        //TODO: Implement
    }

}
