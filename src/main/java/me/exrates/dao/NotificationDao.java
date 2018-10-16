package me.exrates.dao;

import me.exrates.model.main.Notification;
import me.exrates.model.main.NotificationOption;

import java.util.List;

public interface NotificationDao {
    boolean setRead(Long notificationId);

    boolean remove(Long notificationId);

    int setReadAllByUser(Integer idByEmail);

    int removeAllByUser(Integer idByEmail);

    List<Notification> findAllByUser(Integer idByEmail);

    void updateNotificationOptions(List<NotificationOption> options);

    List<NotificationOption> getNotificationOptionsByUser(int userId);

}
