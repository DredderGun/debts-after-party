package dev.avyguzov.debtsafterparty.bot;

import lombok.Getter;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class DebtsAfterPartyBot extends TelegramLongPollingBot {

    private String botToken;
    private String botUsername;
    private TelegramFacade facade;

    @PostConstruct
    public void start() {
        log.info("username: {}, token: {}", botUsername, botToken);
    }

    public DebtsAfterPartyBot(DefaultBotOptions options, TelegramFacade facade) {
        super(options);
        this.facade = facade;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("A new update received");
        log.debug("message: " + update.getMessage());
        try {
            execute(facade.handleUpdate(update));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(long chatId, String textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendInlineKeyBoardMessage(long chatId, String messageText, String buttonText, String callbackData) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton().setText(buttonText);

        if (callbackData != null) {
            keyboardButton.setCallbackData(callbackData);
        }

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(keyboardButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        keyboardMarkup.setKeyboard(rowList);

        try {
            execute(new SendMessage().setChatId(chatId).setText(messageText).setReplyMarkup(keyboardMarkup));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
