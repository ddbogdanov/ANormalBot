package com.ddbogdanov.anormalspringbot.commands.music;

import com.ddbogdanov.anormalspringbot.commands.common.CommandInterface;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Skip extends Command implements CommandInterface<Skip> {
    private EventWaiter waiter;

    @Autowired
    public Skip(EventWaiter waiter) {
        super.name = "!skip";
        super.help = "NOT WORKING RN";
        super.aliases = new String[]{"skip"};
        super.cooldown = 2;
        this.waiter = waiter;
    }

    @Override
    public void setWaiter(EventWaiter waiter) {
        this.waiter = waiter;
    }

    @Override
    protected void execute(CommandEvent event) {
        //TODO: Add skip. I'll have to abstractify the audio manager so all music commands can share the same AudioManager and AudioPlayer
    }
}
