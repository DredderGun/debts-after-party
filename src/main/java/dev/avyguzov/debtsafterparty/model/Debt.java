package dev.avyguzov.debtsafterparty.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Debt implements Serializable {
    private String id;
    private Session session;
    private Participant from;
    private Participant to;
    private double sum;
}
