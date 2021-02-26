package dev.avyguzov.debtsafterparty.bot;

import dev.avyguzov.debtsafterparty.bot.handlers.Handler;
import dev.avyguzov.debtsafterparty.bot.handlers.callbackhandlers.CallbackQueryHandler;
import dev.avyguzov.debtsafterparty.model.State;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.avyguzov.debtsafterparty.model.State.PARTICIPANTS_PROCESSING;

@Component
public class BotStateContext {
    private final Map<State, Handler> stateHandlers = new HashMap<>();
    private final Map<State, CallbackQueryHandler> queryHandlers = new HashMap<>();

    public BotStateContext(List<Handler> handlers, List<CallbackQueryHandler> callbackQueryHandlers) {
        handlers.forEach(handler -> stateHandlers.put(handler.getHandlerName(), handler));
        callbackQueryHandlers.forEach(queryHandler -> queryHandlers.put(queryHandler.getHandlerName(), queryHandler));
    }

    public Handler findMessageHandler(State state) {
        Handler foundHandler;

        if (isParticipantLevelStates(state)) {
            foundHandler = stateHandlers.get(PARTICIPANTS_PROCESSING);
        } else {
            foundHandler = stateHandlers.get(state);
        }

        if (foundHandler == null) {
            throw new IllegalArgumentException("Could`t find an appropriate handler for the state: " + state.toString());
        }

        return foundHandler;
    }

    public CallbackQueryHandler findCallbackQueryHandler(State state) {
        return queryHandlers.get(state);
    }

    private boolean isParticipantLevelStates(State state) {
        switch (state) {
            case PARTICIPANTS_PROCESSING:
            case PARTICIPANTS_ENTERED:
            case PARTICIPANTS_APPROVE_WAITING:
                return true;
        }

        return false;
    }

}
