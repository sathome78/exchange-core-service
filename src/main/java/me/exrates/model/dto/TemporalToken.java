package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.enums.TokenType;

import java.time.LocalDateTime;

@Data
public class TemporalToken {

    private int id;
    private String value;
    private int userId;
    private boolean expired;
    private LocalDateTime dateCreation;
    private TokenType tokenType;
    private String checkIp;
    private boolean isAlreadyUsed;

}
