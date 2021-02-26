package dev.avyguzov.debtsafterparty.service;

import dev.avyguzov.debtsafterparty.model.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class MessageServiceTest {
    private MessageService messageService;
    @Mock
    private LocaleMessageService localeMessageService;
    private long chatId = 1L;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        messageService = new MessageService(localeMessageService);
    }

    @Test
    public void mustFindAppropriateMessageForCurrentSession() {
        messageService.getOrdinaryMessageByState(chatId, State.PARTICIPANTS_PROCESSING);

        Mockito.verify(localeMessageService, Mockito.times(1))
                .getMessage(eq(State.PARTICIPANTS_PROCESSING.toString()), any());
    }

    @Test
    public void mustReturnAppropriateMessageWithArgs() {
        String arg = "argument";
        messageService.getOrdinaryMessageByState(chatId, State.PARTICIPANTS_PROCESSING, arg);

        Mockito.verify(localeMessageService, Mockito.times(1))
                .getMessage(eq(State.PARTICIPANTS_PROCESSING.toString()), eq(arg));
    }
}
