package dev.avyguzov.debtsafterparty.bot;

import dev.avyguzov.debtsafterparty.bot.handlers.Handler;
import dev.avyguzov.debtsafterparty.bot.handlers.callbackhandlers.CallbackQueryFacade;
import dev.avyguzov.debtsafterparty.model.Session;
import dev.avyguzov.debtsafterparty.model.State;
import dev.avyguzov.debtsafterparty.repositories.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static dev.avyguzov.debtsafterparty.model.State.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class TelegramFacadeTest {
    TelegramFacade telegramFacade;

    @Mock
    BotStateContext stateContext;
    @Mock
    SessionRepository sessionRepository;
    @Mock
    CallbackQueryFacade callbackQueryFacade;
    @Mock
    Update update;
    @Mock
    Handler handler;
    @Mock
    Message message;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        telegramFacade = new TelegramFacade(stateContext, sessionRepository, callbackQueryFacade);
    }

    @Test
    public void mustFindCorrectHandlerForNewUser() {
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.hasText()).thenReturn(true);
        User user = new User(1, "FirstName", false, "LastName", "NickName", "RU");
        Mockito.when(message.getFrom()).thenReturn(user);
        Mockito.when(stateContext.findMessageHandler(PARTICIPANTS_PROCESSING)).thenReturn(handler);

        telegramFacade.handleUpdate(update);
        Mockito.verify(stateContext, Mockito.times(1)).findMessageHandler(eq(PARTICIPANTS_PROCESSING));
        Mockito.verify(handler, Mockito.times(1)).processInputMessage(any(Message.class), any(Session.class));

    }

    @Test
    public void mustHandleWithCorrectHandler() {
        Integer tlgrmId = 1;
        State testingState = PARTICIPANTS_ENTERED;

        Session session = new Session(tlgrmId);
        session.setState(testingState);
        Mockito.when(sessionRepository.findSessionByUserId(tlgrmId)).thenReturn(Optional.of(session));
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(message.hasText()).thenReturn(true);
        User user = new User(tlgrmId, "FirstName", false, "LastName", "NickName", "RU");
        Mockito.when(message.getFrom()).thenReturn(user);
        Mockito.when(stateContext.findMessageHandler(testingState)).thenReturn(handler);

        telegramFacade.handleUpdate(update);
        Mockito.verify(stateContext, Mockito.times(1)).findMessageHandler(testingState);
        Mockito.verify(handler, Mockito.times(1)).processInputMessage(any(Message.class), eq(session));
    }

    @Mock
    CallbackQuery callbackQuery;

    @Test
    public void callbackQueryMustBeHandledWithCallbackFacade() {
        Integer tlgrmId = 1;
        User user = new User(tlgrmId, "FirstName", false, "LastName", "NickName", "RU");

        Mockito.when(update.hasCallbackQuery()).thenReturn(true);
        Mockito.when(update.getCallbackQuery()).thenReturn(callbackQuery);
        Mockito.when(update.getMessage()).thenReturn(message);
        Mockito.when(callbackQuery.getFrom()).thenReturn(user);

        telegramFacade.handleUpdate(update);
        Mockito.verify(callbackQueryFacade, Mockito.times(1)).handleCallbackQuery(eq(callbackQuery), any(Session.class));
    }
}
