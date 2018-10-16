package me.exrates.model.dto;

import lombok.Data;

@Data
public class PinAttempsDto {

    private final static int MAX_ATTEMPS = 3;

    int attempsCount = 0;

    public boolean needToSendPin() {
        if (attempsCount == 0 ) {
            attempsCount++;
            return true;
        }
        if (attempsCount >= 3 ) {
            attempsCount = 1;
            return true;
        } else {
            attempsCount++;
            return false;
        }
    }


    public PinAttempsDto() {
    }
}
