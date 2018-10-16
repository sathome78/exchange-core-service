package me.exrates.model.dto;

import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;

public class UpdateUserDto {
    private int id;
    private String email;
    private String phone;
    private UserStatus status;
    private String password;
    private String finpassword;
    private UserRole role;

    /*constructors*/
    public UpdateUserDto(int id) {
        this.id = id;
    }

    /*getters setters*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFinpassword() {
        return finpassword;
    }

    public void setFinpassword(String finpassword) {
        this.finpassword = finpassword;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}