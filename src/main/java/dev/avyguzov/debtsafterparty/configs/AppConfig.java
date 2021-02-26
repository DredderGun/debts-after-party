package dev.avyguzov.debtsafterparty.configs;

import dev.avyguzov.debtsafterparty.bot.DebtsAfterPartyBot;
import dev.avyguzov.debtsafterparty.bot.TelegramFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

@Configuration
public class AppConfig {
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.username}")
    private String botUsername;


    @Bean
    public DebtsAfterPartyBot debtsAfterPartyBot(TelegramFacade telegramFacade) {
        DefaultBotOptions options = ApiContext.getInstance(DefaultBotOptions.class);

        DebtsAfterPartyBot bot = new DebtsAfterPartyBot(options, telegramFacade);
        bot.setBotUsername(botUsername);
        bot.setBotToken(botToken);
        return bot;
    }
}
