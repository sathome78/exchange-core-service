package me.exrates.service.impl;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificationMessagesDao;
import me.exrates.exception.MessageUndeliweredException;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.Notificator;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.service.NotificationMessageService;
import me.exrates.service.NotificatorService;
import me.exrates.service.NotificatorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Maks on 29.09.2017.
 */
@Log4j2(topic = "message_notify")
@Component
public class NotificationMessageServiceImpl implements NotificationMessageService {

    @Autowired
    private NotificatorsService notificatorsService;
    @Autowired
    private NotificationMessagesDao notificationMessagesDao;


    @Override
    @Transactional
    public NotificationResultDto notifyUser(final String userEmail,
                                            final String message,
                                            final String subject,
                                            final NotificationsUserSetting setting) {
        Notificator notificator = Preconditions.checkNotNull(notificatorsService.getById(setting.getNotificatorId()));
        if (!notificator.isEnabled()) {
            notificator = notificatorsService.getById(NotificationTypeEnum.EMAIL.getCode());
        }
        NotificatorService service = notificatorsService.getNotificationServiceByBeanName(notificator.getBeanName());
        NotificationTypeEnum notificationTypeEnum = service.getNotificationType();
        String contactToNotify;
        try {
            contactToNotify = service.sendMessageToUser(userEmail, message, subject);
        } catch (Exception e) {
            log.error(e);
            if (notificationTypeEnum.getCode() != NotificationTypeEnum.EMAIL.getCode()) {
                NotificatorService emailService = notificatorsService.getNotificationService(NotificationTypeEnum.EMAIL.getCode());
                contactToNotify = emailService.sendMessageToUser(userEmail, message, subject);
                notificationTypeEnum = emailService.getNotificationType();
            } else {
                throw new MessageUndeliweredException();
            }
        }
        return getResponseString(setting.getNotificationMessageEventEnum(), notificationTypeEnum, contactToNotify);
    }

    private NotificationResultDto getResponseString(NotificationMessageEventEnum event, NotificationTypeEnum typeEnum, String contactToNotify) {
        String message = notificationMessagesDao.gerResourceString(event, typeEnum);
        return new NotificationResultDto(message, new String[]{contactToNotify});
    }


}
