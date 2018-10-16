package me.exrates.model.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    @JsonIgnore
    private Integer receiverUserId;
    private String title;
    private String message;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationTime;
    @JsonIgnore
    private NotificationEvent cause;
    private Boolean isRead;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Integer receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public NotificationEvent getCause() {
        return cause;
    }

    public void setCause(NotificationEvent cause) {
        this.cause = cause;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", receiverUserId=" + receiverUserId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", creationTime=" + creationTime +
                ", cause=" + cause +
                ", isRead=" + isRead +
                '}';
    }
}
