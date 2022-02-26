package com.ddbogdanov.anormalspringbot.commands.common;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import org.springframework.stereotype.Component;

@Component
public interface CommandInterface<T extends Command> {
    default void setWaiter(EventWaiter waiter) {
        System.err.println(this.getClass().getName() + ": Waiter implementation unnecessary");
    }
}
