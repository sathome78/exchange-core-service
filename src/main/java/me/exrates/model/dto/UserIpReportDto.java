package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@Setter
public class UserIpReportDto {
    private Integer orderNum;
    private Integer id;
    private String nickname;
    private String email;
    private LocalDateTime creationTime;
    private String firstIp;
    private String lastIp;
    private LocalDateTime lastLoginTime;

    public static String getTitle() {
        return Stream.of("No.", "user_id", "nickname", "email",
                "creation_time", "first_ip", "last_ip", "last_login_time")
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.of(String.valueOf(orderNum), String.valueOf(id), nickname, email,
                formattedDateTime(creationTime), emptyIfNull(firstIp), emptyIfNull(lastIp), formattedDateTime(lastLoginTime))
                .collect(Collectors.joining(";", "", "\r\n"));
    }

    private String emptyIfNull(String s) {
        return s == null ? "" : s;
    }

    private String formattedDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
}
