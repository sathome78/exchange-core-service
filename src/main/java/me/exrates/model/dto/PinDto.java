package me.exrates.model.dto;

import lombok.Data;

@Data
public class PinDto {

    private String message;
    private boolean needToSendPin;

    public PinDto(String message, boolean needToSendPin) {
        this.message = message;
        this.needToSendPin = needToSendPin;
    }
}
