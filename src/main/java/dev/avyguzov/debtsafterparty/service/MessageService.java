package dev.avyguzov.debtsafterparty.service;

import dev.avyguzov.debtsafterparty.model.State;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Service
public class MessageService {
    @Value("${btn.cancel.text}")
    private String cancelBtnText;
    @Value("${btn.cancel.data}")
    private String cancelBtnData;
    @Value("${btn.no.text}")
    private String noBtnText;
    @Value("${btn.no.data}")
    private String noBtnData;
    @Value("${btn.yes.text}")
    private String yesBtnText;
    @Value("${btn.yes.data}")
    private String yesBtnData;

    private final LocaleMessageService localeMessageService;

    public MessageService(LocaleMessageService localeMessageService) {
        this.localeMessageService = localeMessageService;
    }

    private List<InlineKeyboardButton> getCancelBtnLine() {
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton().setText(cancelBtnText).setCallbackData(cancelBtnData);
        return Collections.singletonList(keyboardButton);
    }

    private List<InlineKeyboardButton> getYesNoLine() {
        List<InlineKeyboardButton> yesNoLine = new ArrayList<>();
        InlineKeyboardButton yesBtn = new InlineKeyboardButton().setText(yesBtnText);
        yesBtn.setCallbackData(yesBtnData);
        InlineKeyboardButton noBtn = new InlineKeyboardButton().setText(noBtnText);
        noBtn.setCallbackData(noBtnData);
        yesNoLine.add(yesBtn);
        yesNoLine.add(noBtn);
        return yesNoLine;
    }

    public SendMessage getOrdinaryMessageByState(long chatId, State state) {
        return getOrdinaryMessageByState(chatId, state, new Object(){});
    }

    public SendMessage getOrdinaryMessageByPropertyKey(long chatId, String key) {
        return new SendMessage().setChatId(chatId).setText(localeMessageService.getMessage(key));
    }

    public SendMessage getOrdinaryMessageByState(long chatId, State state, Object... args) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(Collections.singletonList(getCancelBtnLine()));

        return new SendMessage().setChatId(chatId)
                .setText(localeMessageService.getMessage(state.toString(), args))
                .setReplyMarkup(keyboardMarkup);
    }

    public SendMessage getYesNoMessage(long chatId, String text) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyBoard = Arrays.asList(
                getCancelBtnLine(),
                getYesNoLine()
        );
        keyboardMarkup.setKeyboard(keyBoard);

        return new SendMessage().setChatId(chatId)
                .setText(text)
                .setReplyMarkup(keyboardMarkup);
    }
}
