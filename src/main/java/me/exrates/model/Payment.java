package me.exrates.model;

import lombok.*;
import me.exrates.model.enums.OperationType;

@Data
public class Payment {
    private int currency;
    private int merchant;
    private double sum;
    private String destination;
    private String destinationTag;
    private String recipient;
    private OperationType operationType;
}