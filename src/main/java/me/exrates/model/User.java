package me.exrates.model;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.main.UserFile;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class User {

    private int id;
    private String nickname;
    private String email;
    private String phone;
    private UserStatus status = UserStatus.REGISTERED;
    private UserStatus userStatus = UserStatus.REGISTERED;
    private String password;
    private String finpassword;
    private Date regdate;
    private String ipaddress;
    private String confirmPassword;
    private String confirmFinPassword;
    private boolean readRules;
    private UserRole role = UserRole.USER;
    private String parentEmail;
    private List<UserFile> userFiles = Collections.emptyList();
    private Integer apiRateLimit;

    public User() {
    }

}