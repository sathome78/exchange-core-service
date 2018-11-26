package me.exrates.model;

import javax.validation.constraints.NotNull;

public class UserAuthenticationDto {

    @NotNull(message = "Email is missing")
    private String email;
    @NotNull(message = "Password is missing")
    private String password;
    @NotNull(message = "Application key is missing")
    private String appKey;

    private String clientIp;

    private String pin;

    private boolean isPinRequired;

    public UserAuthenticationDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public boolean isPinRequired() {
        return isPinRequired;
    }

    public void setPinRequired(boolean pinRequired) {
        isPinRequired = pinRequired;
    }

    @Override
    public String toString() {
        return "UserAuthenticationDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", appKey='" + appKey + '\'' +
                '}';
    }
}