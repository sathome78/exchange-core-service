package me.exrates.service;

import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.NotificationsUserSetting;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Maks on 29.09.2017.
 */
public interface NotificationMessageService {


    @Transactional
    NotificationResultDto notifyUser(String userEmail,
                                     String message,
                                     String subject,
                                     NotificationsUserSetting setting);
}
