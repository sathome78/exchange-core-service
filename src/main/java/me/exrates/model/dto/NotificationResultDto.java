package me.exrates.model.dto;

import lombok.Data;

@Data
public class NotificationResultDto {

    private String messageSource;
    private String[] arguments;


    public NotificationResultDto(String messageSource, String[] arguments) {
        this.messageSource = messageSource;
        this.arguments = arguments;
    }
}
