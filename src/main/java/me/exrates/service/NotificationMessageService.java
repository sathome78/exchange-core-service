package me.exrates.service;

import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.NotificationsUserSetting;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationMessageService {

    @Transactional
    NotificationResultDto notifyUser(String userEmail,
                                     String message,
                                     String subject,
                                     NotificationsUserSetting setting);
}
