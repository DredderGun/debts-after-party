package dev.avyguzov.debtsafterparty.bot.handlers;

import dev.avyguzov.debtsafterparty.model.Session;
import dev.avyguzov.debtsafterparty.model.State;
import dev.avyguzov.debtsafterparty.repositories.SessionRepository;
import dev.avyguzov.debtsafterparty.service.MessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class ParticipantsHandlerTest {
    @Mock
    SessionRepository repository;
    @Mock
    Message message;
    @Mock
    MessageService messageService;
    ParticipantsHandler participantsHandler;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        participantsHandler = new ParticipantsHandler(messageService, repository);
    }

    @Test
    public void getCorrectHandlerName() {
        Assertions.assertEquals(State.PARTICIPANTS_PROCESSING, participantsHandler.getHandlerName());
    }

    @Test
    public void correctStateAfterStartMessage() {
        Session session = createSessionAndSpecificState(State.PARTICIPANTS_PROCESSING);
        participantsHandler.processInputMessage(message, session);
        Assertions.assertEquals(session.getState(), State.PARTICIPANTS_ENTERED);
        Mockito.verify(repository, Mockito.times(1)).save(eq(session));
    }

    @Test
    public void correctActionsInParticipantProcessingState() {
        Session session = createSessionAndSpecificState(State.PARTICIPANTS_ENTERED);
        Mockito.when(message.getChatId()).thenReturn(1L);

        participantsHandler.processInputMessage(message, session);
        Assertions.assertEquals(session.getState(), State.PARTICIPANTS_ENTERED);
        Mockito.verify(repository, Mockito.times(1)).save(eq(session));
    }

    private Session createSessionAndSpecificState(State state) {
        Session session = new Session(1);
        session.setState(state);
        return session;
    }

    @Test
    public void correctStateAfterSuccessParticipantsInput() {
        Session session = createSessionAndSpecificState(State.PARTICIPANTS_ENTERED);
        messageMockInit("Саша,Петя,Вася");

        participantsHandler.processInputMessage(message, session);
        Assertions.assertEquals(session.getState(), State.PARTICIPANTS_APPROVE_WAITING);
        Mockito.verify(repository, Mockito.times(1)).save(eq(session));
    }

    private void messageMockInit(String messageText) {
        Mockito.when(message.getChatId()).thenReturn(1L);
        Mockito.when(message.hasText()).thenReturn(true);
        Mockito.when(message.getText()).thenReturn(messageText);
    }

    @Test
    public void mustBeParticipantEnteredStateWhenIncorrectMessageFormat() {
        Session session = createSessionAndSpecificState(State.PARTICIPANTS_ENTERED);
        messageMockInit("Саша,Петя !$%");

        participantsHandler.processInputMessage(message, session);
        Assertions.assertEquals(session.getState(), State.PARTICIPANTS_ENTERED);
        Mockito.verify(repository, Mockito.times(1)).save(eq(session));
    }

    @Test
    public void mustBeParticipantEnteredStateWhenMessageConsistsOnlySpaces() {
        Session session = createSessionAndSpecificState(State.PARTICIPANTS_ENTERED);
        messageMockInit("            ");

        participantsHandler.processInputMessage(message, session);
        Assertions.assertEquals(session.getState(), State.PARTICIPANTS_ENTERED);
        Mockito.verify(repository, Mockito.times(1)).save(eq(session));
    }

    @Test
    public void correctStateWhenEmptyMessage() {
        Session session = createSessionAndSpecificState(State.PARTICIPANTS_ENTERED);
        Mockito.when(message.getChatId()).thenReturn(1L);
        Mockito.when(message.hasText()).thenReturn(false);

        participantsHandler.processInputMessage(message, session);
        Assertions.assertEquals(session.getState(), State.PARTICIPANTS_ENTERED);
        Mockito.verify(repository, Mockito.times(1)).save(eq(session));
    }

    @Test
    public void replyMessageContainAllParticipantsNames() {
        Session session = createSessionAndSpecificState(State.PARTICIPANTS_ENTERED);
        messageMockInit("Саша,Петя,Вася, Жора");

        ArgumentCaptor<String> textToMessage = ArgumentCaptor.forClass(String.class);
        participantsHandler.processInputMessage(message, session);

        Mockito.verify(messageService).getYesNoMessage(any(long.class), textToMessage.capture());
        Assertions.assertTrue(textToMessage.getValue().contains("Саша"));
        Assertions.assertTrue(textToMessage.getValue().contains("Петя"));
        Assertions.assertTrue(textToMessage.getValue().contains("Вася"));
        Assertions.assertTrue(textToMessage.getValue().contains("Жора"));
    }

    @Test
    public void correctMessageWhenHasSameNames() {
        Session session = createSessionAndSpecificState(State.PARTICIPANTS_ENTERED);
        messageMockInit("Петя,Петя,Петя");

        ArgumentCaptor<String> textToMessage = ArgumentCaptor.forClass(String.class);
        participantsHandler.processInputMessage(message, session);

        Mockito.verify(messageService).getYesNoMessage(any(long.class), textToMessage.capture());
        Assertions.assertTrue(textToMessage.getValue().contains("Петя"));
        Assertions.assertTrue(textToMessage.getValue().contains("Петя-1"));
        Assertions.assertTrue(textToMessage.getValue().contains("Петя-2"));
    }

    @Test
    public void writeAllParticipantsWithCorrectNamesToDb() {
        Session session = createSessionAndSpecificState(State.PARTICIPANTS_ENTERED);
        messageMockInit("Саша,Петя,Маша");

        participantsHandler.processInputMessage(message, session);

        Mockito.verify(repository, Mockito.times(1)).save(eq(session));


        Assertions.assertEquals(
            1,
                session.getParticipants().stream().filter(participant -> participant.getName().equals("Саша")).count()
        );
        Assertions.assertEquals(
            1,
                session.getParticipants().stream().filter(participant -> participant.getName().equals("Петя")).count()
        );
        Assertions.assertEquals(
            1,
                session.getParticipants().stream().filter(participant -> participant.getName().equals("Маша")).count()
        );
    }
}
