package dev.avyguzov.debtsafterparty.bot;

import dev.avyguzov.debtsafterparty.bot.handlers.callbackhandlers.ButtonType;
import dev.avyguzov.debtsafterparty.bot.handlers.callbackhandlers.CallbackQueryFacade;
import dev.avyguzov.debtsafterparty.bot.handlers.callbackhandlers.CallbackQueryHandler;
import dev.avyguzov.debtsafterparty.model.Session;
import dev.avyguzov.debtsafterparty.repositories.SessionRepository;
import dev.avyguzov.debtsafterparty.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import static dev.avyguzov.debtsafterparty.model.State.PARTICIPANTS_APPROVE_WAITING;
import static org.mockito.ArgumentMatchers.any;

public class CallbackQueryFacadeTest {
    CallbackQueryFacade callbackQueryFacade;
    @Mock
    BotStateContext botStateContext;
    @Mock
    SessionRepository sessionRepository;
    @Mock
    CallbackQuery userQuery;
    @Mock
    MessageService messageService;
    @Mock
    Message mockMessage;
    Long chatId = 1L;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(messageService.getOrdinaryMessageByPropertyKey(any(Long.class), any(String.class))).thenReturn(new SendMessage());
        mockMessage = Mockito.mock(Message.class);
        Mockito.when(mockMessage.getChatId()).thenReturn(chatId);
        Mockito.when(userQuery.getMessage()).thenReturn(mockMessage);

        callbackQueryFacade = new CallbackQueryFacade(botStateContext, sessionRepository, messageService);
    }

    @Test
    public void deleteSessionWhenCancelBtnPushed() {
        Integer currTelegramUserId = 1;
        Mockito.when(userQuery.getData()).thenReturn(ButtonType.CANCEL.toString());
        callbackQueryFacade.handleCallbackQuery(userQuery, new Session(currTelegramUserId));
        Mockito.verify(sessionRepository).deleteById(currTelegramUserId);
    }

    @Test
    public void whenCancelButtonThenGetStartMessageFromMessageService() {
        Integer currTelegramUserId = 1;
        Mockito.when(userQuery.getData()).thenReturn(ButtonType.CANCEL.toString());

        callbackQueryFacade.handleCallbackQuery(userQuery, new Session(currTelegramUserId));
        Mockito.verify(messageService, Mockito.times(1)).getOrdinaryMessageByPropertyKey(chatId, "startMessage");
    }

    @Mock
    CallbackQueryHandler callbackQueryHandler;

    @Test
    public void mustFindAndApplyCorrectHandler() throws Exception {
        Integer currTelegramUserId = 1;
        Session currSession = new Session(currTelegramUserId);
        currSession.setState(PARTICIPANTS_APPROVE_WAITING);

        Mockito.when(botStateContext.findCallbackQueryHandler(PARTICIPANTS_APPROVE_WAITING)).thenReturn(callbackQueryHandler);
        Mockito.when(userQuery.getData()).thenReturn(ButtonType.YES.toString());

        callbackQueryFacade.handleCallbackQuery(userQuery, currSession);
        Mockito.verify(callbackQueryHandler).handleCallbackQuery(userQuery, currSession);
    }
}
