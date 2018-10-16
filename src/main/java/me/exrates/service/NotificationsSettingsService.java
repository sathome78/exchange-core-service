package me.exrates.service;

import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;

import java.util.Map;

public interface NotificationsSettingsService {

    NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event);

    void createOrUpdate(NotificationsUserSetting setting);

    Object get2faOptionsForUser(int id);

    Map<Integer, NotificationsUserSetting> getSettingsMap(int userId);
}
