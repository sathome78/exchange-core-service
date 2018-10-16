package me.exrates.model.main;

import me.exrates.model.enums.NotificationEvent;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Created by OLEG on 15.11.2016.
 */
public class NotificationOption {
    private NotificationEvent event;
    private Integer userId;
    private boolean sendNotification;
    private boolean sendEmail;

    private String eventLocalized;

    public NotificationEvent getEvent() {
        return event;
    }

    public void setEvent(NotificationEvent event) {
        this.event = event;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getEventLocalized() {
        return eventLocalized;
    }

    public void localize(MessageSource messageSource, Locale locale) {
        eventLocalized = event.toLocalizedString(messageSource, locale);
    }

    @Override
    public String toString() {
        return "NotificationOption{" +
                "event=" + event +
                ", userId=" + userId +
                ", sendNotification=" + sendNotification +
                ", sendEmail=" + sendEmail +
                '}';
    }
}
