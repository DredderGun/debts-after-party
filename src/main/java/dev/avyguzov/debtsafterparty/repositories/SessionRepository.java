package dev.avyguzov.debtsafterparty.repositories;

import dev.avyguzov.debtsafterparty.model.Session;

import java.util.Optional;

public interface SessionRepository {
    Optional<Session> findSessionByUserId(Integer userTelegramId);
    void save(Session session);
    void deleteById(Integer id);
}