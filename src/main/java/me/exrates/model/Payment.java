package me.exrates.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OperationType;
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Payment {
    private int currency;
    private int merchant;
    private double sum;
    private String destination;
    private String destinationTag;
    private String recipient;
    private OperationType operationType;

    public Payment(OperationType operationType) {
        this.operationType = operationType;
    }
}