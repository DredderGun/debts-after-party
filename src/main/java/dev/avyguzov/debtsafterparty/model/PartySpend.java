package dev.avyguzov.debtsafterparty.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class PartySpend implements Serializable {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private Session session;
    @NotNull
    private List<Participant> usedBy;
}
