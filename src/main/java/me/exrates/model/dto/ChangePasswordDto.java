package me.exrates.model.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    /*old password*/
    private String password;
    /*new password*/
    private String confirmPassword;
}
