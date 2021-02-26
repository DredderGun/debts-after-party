package dev.avyguzov.debtsafterparty.bot;

import dev.avyguzov.debtsafterparty.bot.handlers.callbackhandlers.CallbackQueryFacade;
import dev.avyguzov.debtsafterparty.model.Session;
import dev.avyguzov.debtsafterparty.repositories.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static dev.avyguzov.debtsafterparty.model.State.*;

/*
 * Распределяет сообщение в соответсвии с состоянием юзера, распределяет обработчики для кнопок.
 *
 */
@Service
@Slf4j
public class TelegramFacade {
    private final BotStateContext stateContext;
    private final SessionRepository sessionRepository;
    private final CallbackQueryFacade callbackQueryFacade;

    public TelegramFacade(BotStateContext stateContext,
                          SessionRepository sessionRepository,
                          CallbackQueryFacade callbackQueryFacade) {
        this.stateContext = stateContext;
        this.sessionRepository = sessionRepository;
        this.callbackQueryFacade = callbackQueryFacade;
    }

    public SendMessage handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User: {} with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getData());
            Session session = getCurrentSessionOrCreateNew(update.getCallbackQuery().getFrom().getId());
            return callbackQueryFacade.handleCallbackQuery(update.getCallbackQuery(), session);
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            return handleInputMessage(message.getFrom().getId(), message);
        }

        throw new IllegalArgumentException("Could`t find message or query callback in the message");
    }

    private SendMessage handleInputMessage(Integer userId, Message msg) {
        Session session = getCurrentSessionOrCreateNew(userId);
        return stateContext.findMessageHandler(session.getState()).processInputMessage(msg, session);
    }

    private Session getCurrentSessionOrCreateNew(Integer telegramUserId) {
        log.info("Getting user with id={} from memory", telegramUserId);
        return sessionRepository.findSessionByUserId(telegramUserId)
                .orElseGet(() -> {
                    Session newSession = new Session(telegramUserId);
                    newSession.setState(PARTICIPANTS_PROCESSING);
                    sessionRepository.save(newSession);
                    log.info("Created new session for user with id: {}", telegramUserId);
                    return newSession;
                });
    }

}
