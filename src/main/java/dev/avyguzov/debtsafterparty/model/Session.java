package dev.avyguzov.debtsafterparty.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Data
public class Session {
    @NotNull
    private final Integer telegramUserId;
    @NotNull
    private State state;
    private Set<Participant> participants = new HashSet<>();
    private Set<PartySpend> partySpends = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return telegramUserId.equals(session.telegramUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(telegramUserId);
    }
}