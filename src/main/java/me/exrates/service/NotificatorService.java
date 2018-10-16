package me.exrates.service;

import me.exrates.exception.MessageUndeliweredException;
import me.exrates.model.enums.NotificationTypeEnum;

public interface NotificatorService {


    Object getSubscriptionByUserId(int userId);

    String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException;

    NotificationTypeEnum getNotificationType();

}
