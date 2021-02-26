package dev.avyguzov.debtsafterparty.bot.handlers.callbackhandlers;

import dev.avyguzov.debtsafterparty.model.Session;
import dev.avyguzov.debtsafterparty.model.State;
import dev.avyguzov.debtsafterparty.service.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static dev.avyguzov.debtsafterparty.model.State.*;

@Component
@AllArgsConstructor
@Slf4j
public class ParticipantsCallbackHandler implements CallbackQueryHandler {
    private final MessageService messageService;

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery, Session session) throws Exception {
        log.info("ParticipantsCallbackHandler started a method handleCallbackQuery with callbackQuery data: " + callbackQuery.getData());

        if (callbackQuery.getData() == null) {
            throw new Exception("CallbackQuery doesn`t have data");
        }

        if (callbackQuery.getData().equals(ButtonType.YES.toString())) {
            session.setState(SPENDS_PROCESSING);
        } else if (callbackQuery.getData().equals(ButtonType.NO.toString())) {
            session.getParticipants().clear();
            session.setState(PARTICIPANTS_PROCESSING);
        } else {
            throw new Exception("Illegal button type was received by handler in PARTICIPANTS_APPROVE_WAITING state");
        }

        return messageService.getOrdinaryMessageByState(callbackQuery.getMessage().getChatId(), session.getState());
    }

    @Override
    public State getHandlerName() {
        return PARTICIPANTS_APPROVE_WAITING;
    }
}
