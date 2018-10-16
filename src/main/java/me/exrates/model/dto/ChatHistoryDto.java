package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class ChatHistoryDto {

    private String email;
    private String body;
    private String messageTime;

    public static String getTitle() {
        return Stream.of("email", "body", "message_time")
                .collect(Collectors.joining(";", "", "\r\n"));
    }

    @Override
    public String toString() {
        return Stream.of(
                email,
                body,
                messageTime
        ).collect(Collectors.joining(";", "", "\r\n"));
    }
}
