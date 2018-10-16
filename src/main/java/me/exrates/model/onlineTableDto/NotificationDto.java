package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class NotificationDto extends OnlineTableDto {
    private Long id;
    @JsonIgnore
    private Integer receiverUserId;
    private String title;
    private String message;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationTime;
    private Boolean isRead;

    public NotificationDto() {
        this.needRefresh = true;
    }

    public NotificationDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

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

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
