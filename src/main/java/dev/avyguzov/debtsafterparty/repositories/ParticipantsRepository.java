package dev.avyguzov.debtsafterparty.repositories;

import dev.avyguzov.debtsafterparty.model.Participant;

import java.util.Collection;

public interface ParticipantsRepository {
    int[] saveAll(Collection<Participant> participants);
}
