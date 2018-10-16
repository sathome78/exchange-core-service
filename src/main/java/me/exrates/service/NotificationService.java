package me.exrates.service;

import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.main.Notification;
import me.exrates.model.main.NotificationOption;

import java.util.List;

public interface NotificationService {
    List<Notification> findAllByUser(String name);

    boolean setRead(Long notificationId);

    boolean remove(Long notificationId);

    int setReadAllByUser(String name);

    int removeAllByUser(String name);

    List<NotificationOption> getNotificationOptionsByUser(int id);

    void updateUserNotifications(List<NotificationOption> notificationOptions);

    long createLocalizedNotification(Integer parent, NotificationEvent inOut, String s, String s1, Object[] objects);
}
