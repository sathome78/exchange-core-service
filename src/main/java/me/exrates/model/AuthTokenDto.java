package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

public class AuthTokenDto {
    private String token;
    private String nickname;
    @JsonProperty(value = "id")
    private Integer userId;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String avatarPath;
    @JsonProperty(value = "language")
    private Locale locale;
    private Boolean finPasswordSet;
    private String referralReference;

    public AuthTokenDto() {
    }

    public AuthTokenDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Boolean getFinPasswordSet() {
        return finPasswordSet;
    }

    public void setFinPasswordSet(Boolean finPasswordSet) {
        this.finPasswordSet = finPasswordSet;
    }

    public String getReferralReference() {
        return referralReference;
    }

    public void setReferralReference(String referralReference) {
        this.referralReference = referralReference;
    }

    @Override
    public String toString() {
        return "AuthTokenDto{" +
                "token='" + token + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userId=" + userId +
                ", avatarPath='" + avatarPath + '\'' +
                ", locale=" + locale +
                ", finPasswordSet=" + finPasswordSet +
                ", referralReference='" + referralReference + '\'' +
                '}';
    }
}
