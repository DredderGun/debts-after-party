package dev.avyguzov.debtsafterparty.bot.handlers;

import dev.avyguzov.debtsafterparty.model.Participant;
import dev.avyguzov.debtsafterparty.model.Session;
import dev.avyguzov.debtsafterparty.model.State;
import dev.avyguzov.debtsafterparty.repositories.SessionRepository;
import dev.avyguzov.debtsafterparty.service.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

import static dev.avyguzov.debtsafterparty.model.State.*;

/*
 * Switch user state and handle participant messages.
 *
 */
@Component
@Slf4j
@AllArgsConstructor
public class ParticipantsHandler extends Handler {
    MessageService messageService;
    SessionRepository repository;
    @Override
    public State getHandlerName() {
        return State.PARTICIPANTS_PROCESSING;
    }

    private boolean validateRequestMessage(String text) {
        return text.trim().length() != 0 && text.matches("^[\\.a-zA-Zа-яА-Я0-9,!? ]*$");
    }

    @Override
    public SendMessage processInputMessage(Message message, Session session) {
        log.info("ParticipantsHandler get message");

        SendMessage resultMessage;

        switch (session.getState()) {
            case PARTICIPANTS_PROCESSING:
                resultMessage = messageService.getOrdinaryMessageByState(message.getChatId(), session.getState());
                session.setState(PARTICIPANTS_ENTERED);
                log.info("User with id={} switched the state to PARTICIPANTS_ENTERED", session.getTelegramUserId());
                break;
            case PARTICIPANTS_ENTERED:
                if (message.hasText() && message.getText() != null && validateRequestMessage(message.getText())) {
                    parseParticipantsNamesAndSaveToSession(session, message.getText());
                    session.setState(PARTICIPANTS_APPROVE_WAITING);
                    resultMessage = messageService.getYesNoMessage(message.getChatId(), "Вы ввели следующих участников: " +
                            session.getParticipants().stream()
                                    .reduce(
                                            "",
                                            (partialResult, currEl) -> partialResult + "\n" + currEl.getName(),
                                            String::concat
                                    ) + "\n\n" + "Всё верно?");
                    log.info("User with id={} switched the state to PARTICIPANTS_APPROVE_WAITING", session.getTelegramUserId());
                } else {
                    resultMessage = messageService.getOrdinaryMessageByState(message.getChatId(), session.getState());
                    log.info("User with id={} failed the validation", session.getTelegramUserId());
                }
                break;
            default:
                throw new IllegalArgumentException("No state defined");
        }

        repository.save(session);
        return resultMessage;
    }

    private void parseParticipantsNamesAndSaveToSession(Session session, String textWithParticipants) {
        Set<String> participantsNames = new HashSet<>();
        Map<String, Integer> copiesCountNames = new HashMap<>();
        for (String participantName : textWithParticipants.split(",")) {
            String currentUserName = participantName.trim();
            if (!participantsNames.add(currentUserName)) {
                currentUserName = currentUserName + "-" + copiesCountNames.merge(currentUserName, 1, Integer::sum);
                participantsNames.add(currentUserName);
            }
            session.getParticipants().add(new Participant(currentUserName, session));
        }
    }
}
