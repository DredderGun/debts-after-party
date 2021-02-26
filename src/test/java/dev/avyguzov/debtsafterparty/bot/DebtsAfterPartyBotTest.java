package dev.avyguzov.debtsafterparty.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.verify;

public class DebtsAfterPartyBotTest {
//    DebtsAfterPartyBot debtsAfterPartyBot = new DebtsAfterPartyBot();
    @Mock
    Update update;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void startMessageCheck() {
//        debtsAfterPartyBot.onUpdateReceived(update);
//        verify(debtsAfterPartyBot).sendApiMethod();
    }

}
