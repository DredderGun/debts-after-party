package dev.avyguzov.debtsafterparty.repositories.impl;

import dev.avyguzov.debtsafterparty.model.Session;
import dev.avyguzov.debtsafterparty.repositories.SessionRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class InMemoryRepository implements SessionRepository {
    private final HashMap<Integer, Session> sessions = new HashMap<>();

    @Override
    public Optional<Session> findSessionByUserId(Integer userTelegramId) {
        return Optional.ofNullable(sessions.get(userTelegramId));
    }

    @Override
    public void save(Session session) {
        sessions.put(session.getTelegramUserId(), session);
    }

    @Override
    public void deleteById(Integer id) {
        sessions.remove(id);
    }
}
