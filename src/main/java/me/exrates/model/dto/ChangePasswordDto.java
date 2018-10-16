package me.exrates.model.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String password;
    private String confirmPassword;
}
