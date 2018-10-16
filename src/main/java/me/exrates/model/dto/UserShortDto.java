package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.UserStatus;

@Getter
@Setter
@ToString
public class UserShortDto {
    private Integer id;
    private String email;
    private String password;
    private UserStatus status;
}
