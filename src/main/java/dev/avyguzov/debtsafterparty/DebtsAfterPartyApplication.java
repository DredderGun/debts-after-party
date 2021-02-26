package dev.avyguzov.debtsafterparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class DebtsAfterPartyApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();

        SpringApplication.run(DebtsAfterPartyApplication.class, args);
    }

}
