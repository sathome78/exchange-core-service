package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.service.NotificationsSettingsService;
import me.exrates.service.NotificatorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j2(topic = "message_notify")
@Component
public class NotificationsSettingsServiceImpl implements NotificationsSettingsService {

    @Autowired
    private NotificationUserSettingsDao settingsDao;
    @Autowired
    private NotificatorsService notificatorsService;


    @Override
    public NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event) {
        return settingsDao.getByUserAndEvent(userId, event);
    }

    @Override
    public void createOrUpdate(NotificationsUserSetting setting) {
        if (getByUserAndEvent(setting.getUserId(), setting.getNotificationMessageEventEnum()) == null) {
            settingsDao.create(setting);
        } else {
            settingsDao.update(setting);
        }
    }

    @Override
    public Map<String, Object> get2faOptionsForUser(int userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("notificators", notificatorsService.getAllNotificators());
        map.put("events", Arrays.asList(NotificationMessageEventEnum.values()));
        map.put("settings", setDefaultSettings(userId, getSettingsMap(userId)));
        map.put("subscriptions", notificatorsService.getSubscriptions(userId));
        return map;
    }

    @Override
    public Map<Integer, NotificationsUserSetting> getSettingsMap(int userId) {
        HashMap<Integer, NotificationsUserSetting> settingsMap = new HashMap<>();
        Arrays.asList(NotificationMessageEventEnum.values()).forEach(p -> {
                    settingsMap.put(p.getCode(), getByUserAndEvent(userId, p));
                }
        );
        return settingsMap;
    }

    private Map<Integer, NotificationsUserSetting> setDefaultSettings(int userId, Map<Integer, NotificationsUserSetting> map) {
        Arrays.asList(NotificationMessageEventEnum.values()).forEach(p -> {
                    NotificationsUserSetting setting = map.get(p.getCode());
                    if ((setting == null || setting.getNotificatorId() == null) && !p.isCanBeDisabled())
                        map.put(p.getCode(), NotificationsUserSetting.builder()
                                .notificatorId(NotificationTypeEnum.EMAIL.getCode())
                                .userId(userId)
                                .notificationMessageEventEnum(p)
                                .build());
                }
        );
        return map;
    }

}
