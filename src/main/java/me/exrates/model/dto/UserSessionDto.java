package me.exrates.model.dto;

import me.exrates.model.enums.UserRole;

/**
 * Created by OLEG on 07.09.2016.
 */
public class UserSessionDto {
    private int userId;
    private String sessionId;
    private String userNickname;
    private String userEmail;
    private UserRole userRole;

    public UserSessionDto(UserSessionInfoDto userSessionInfoDto, String sessionId) {
        this.userId = userSessionInfoDto.getUserId();
        this.sessionId = sessionId;
        this.userNickname = userSessionInfoDto.getUserNickname();
        this.userEmail = userSessionInfoDto.getUserEmail();
        this.userRole = userSessionInfoDto.getUserRole();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "UserSessionInfoDto{" +
                "userId=" + userId +
                ", sessionId=" + sessionId +
                ", userNickname='" + userNickname + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userRole=" + userRole +
                '}';
    }
}
