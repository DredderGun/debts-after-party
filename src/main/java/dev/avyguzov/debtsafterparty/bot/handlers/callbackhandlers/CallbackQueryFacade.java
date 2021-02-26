package dev.avyguzov.debtsafterparty.bot.handlers.callbackhandlers;

import dev.avyguzov.debtsafterparty.bot.BotStateContext;
import dev.avyguzov.debtsafterparty.model.Session;
import dev.avyguzov.debtsafterparty.repositories.SessionRepository;
import dev.avyguzov.debtsafterparty.service.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@AllArgsConstructor
@Slf4j
public class CallbackQueryFacade {
    private final BotStateContext botStateContext;
    private final SessionRepository sessionRepository;
    private final MessageService messageService;

    public SendMessage handleCallbackQuery(CallbackQuery usersQuery, Session session) {
        ButtonType button = ButtonType.valueOf(usersQuery.getData().split("\\|")[0]);

        if (button.equals(ButtonType.CANCEL)) {
            sessionRepository.deleteById(session.getTelegramUserId());
            return messageService.getOrdinaryMessageByPropertyKey(usersQuery.getMessage().getChatId(), "startMessage");
        }

        try {
            return botStateContext.findCallbackQueryHandler(session.getState()).handleCallbackQuery(usersQuery, session);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return messageService.getOrdinaryMessageByPropertyKey(usersQuery.getMessage().getChatId(), "errorMessage");
        }

    }
}