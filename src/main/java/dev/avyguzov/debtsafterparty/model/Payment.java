package dev.avyguzov.debtsafterparty.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Payment {
    private Long id;
    private Participant who;
    private PartySpend what;
    private BigDecimal howMuch;
}
