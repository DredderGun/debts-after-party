package dev.avyguzov.debtsafterparty.bot.handlers;

import dev.avyguzov.debtsafterparty.model.Session;
import dev.avyguzov.debtsafterparty.model.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class Handler {
    public abstract SendMessage processInputMessage(Message message, Session session);
    public abstract State getHandlerName();
}
