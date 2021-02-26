package dev.avyguzov.debtsafterparty.bot.handlers.callbackhandlers;

import dev.avyguzov.debtsafterparty.model.Participant;
import dev.avyguzov.debtsafterparty.model.Session;
import dev.avyguzov.debtsafterparty.model.State;
import dev.avyguzov.debtsafterparty.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class ParticipantsCallbackHandlerTest {
    @Mock
    private CallbackQuery callbackQuery;
    @Mock
    private MessageService messageService;
    @Mock
    private Message message;
    private ParticipantsCallbackHandler participantsYesCallbackHandler;
    private User telegramUser;
    private Long chatId = 1L;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(callbackQuery.getData()).thenReturn(ButtonType.YES.toString());
        Mockito.when(messageService.getOrdinaryMessageByState(Mockito.any(long.class), Mockito.any(State.class))).thenReturn(new SendMessage());
        telegramUser = new User(1, "FirstName", false, "LastName", "NickName", "RU");
        Mockito.when(callbackQuery.getFrom()).thenReturn(telegramUser);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(callbackQuery.getMessage()).thenReturn(message);

        participantsYesCallbackHandler = new ParticipantsCallbackHandler(messageService);
    }

    @Test
    public void mustSetSpendsProcessingToSessionState() throws Exception {
        Session currSession = new Session(1);
        Mockito.when(callbackQuery.getData()).thenReturn(ButtonType.YES.toString());

        participantsYesCallbackHandler.handleCallbackQuery(callbackQuery, currSession);
        Assertions.assertEquals(currSession.getState(), State.SPENDS_PROCESSING);
    }

    @Test
    public void clearAllParticipantsWhenButtonNo() throws Exception {
        Session currSession = new Session(1);
        currSession.getParticipants().add(new Participant("Вася", currSession));
        Mockito.when(callbackQuery.getData()).thenReturn(ButtonType.NO.toString());

        participantsYesCallbackHandler.handleCallbackQuery(callbackQuery, currSession);
        Assertions.assertEquals(0, currSession.getParticipants().size());
    }

    @Test
    public void spendsProcessingStateWhenButtonNo() throws Exception {
        Session currSession = new Session(1);
        currSession.setState(State.PARTICIPANTS_ENTERED);
        Mockito.when(callbackQuery.getData()).thenReturn(ButtonType.NO.toString());

        participantsYesCallbackHandler.handleCallbackQuery(callbackQuery, currSession);
        Assertions.assertEquals(currSession.getState(), State.PARTICIPANTS_PROCESSING);
    }
}
