package me.exrates.service;

import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.main.Notification;
import me.exrates.model.main.NotificationOption;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

public interface NotificationService {

    @Transactional(rollbackFor = Exception.class)
    void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                    Object[] messageArgs, Locale locale);

    @Transactional(rollbackFor = Exception.class)
    void notifyUser(Integer userId, NotificationEvent cause, String titleMessage, String message);

    List<Notification> findAllByUser(String name);

    boolean setRead(Long notificationId);

    boolean remove(Long notificationId);

    int setReadAllByUser(String name);

    int removeAllByUser(String name);

    List<NotificationOption> getNotificationOptionsByUser(int id);

    void updateUserNotifications(List<NotificationOption> notificationOptions);

    long createLocalizedNotification(Integer parent, NotificationEvent inOut, String s, String s1, Object[] objects);

    void notifyUser(Integer userId, NotificationEvent order, String s, String s1, Object[] objects);

    void updateNotificationOptionsForUser(int userId, List<NotificationOption> options);
}
