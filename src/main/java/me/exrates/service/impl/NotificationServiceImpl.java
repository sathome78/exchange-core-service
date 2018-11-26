package me.exrates.service.impl;

import me.exrates.dao.NotificationDao;
import me.exrates.model.Email;
import me.exrates.model.User;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.main.Notification;
import me.exrates.model.main.NotificationOption;
import me.exrates.service.NotificationService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private UserService userService;

    @Autowired
    private SendMailService sendMailService;

    @Override
    public long createLocalizedNotification(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                                            Object[] messageArgs) {
        Locale locale = new Locale(userService.getPreferedLang(userId));
        return 0L;
    }

    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs) {
        String lang = userService.getPreferedLang(userId);
        Locale locale = new Locale(StringUtils.isEmpty(lang) ? "EN" : lang);
        notifyUser(userId, cause, titleCode, messageCode, normalizeArgs(messageArgs), locale);
    }

    @Override
    public void updateNotificationOptionsForUser(int userId, List<NotificationOption> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs, Locale locale) {
        String titleMessage = messageSource.getMessage(titleCode, null, locale);
        String message = messageSource.getMessage(messageCode, normalizeArgs(messageArgs), locale);
        notifyUser(userId, cause, titleMessage, message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(Integer userId, NotificationEvent cause, String titleMessage, String message) {
        User user = userService.getUserById(userId);
        /*NotificationOption option = notificationDao.findUserOptionForEvent(userId, cause);*/
      /*if (option.isSendNotification()) {
        createNotification(
            userId,
            titleMessage,
            message,
            cause);
      }*/
        /*Always on email notifications*/
        if (true/*option.isSendEmail()*/) {
            Email email = new Email();
            email.setSubject(titleMessage);
            email.setMessage(message);
            email.setTo(user.getEmail());
            sendMailService.sendInfoMail(email);
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> findAllByUser(String email) {
        return notificationDao.findAllByUser(userService.getIdByEmail(email));
    }

    @Override
    public boolean setRead(Long notificationId) {
        return notificationDao.setRead(notificationId);
    }

    @Override
    public boolean remove(Long notificationId) {
        return notificationDao.remove(notificationId);
    }

    @Override
    public int setReadAllByUser(String email) {
        return notificationDao.setReadAllByUser(userService.getIdByEmail(email));
    }

    @Override
    public int removeAllByUser(String email) {
        return notificationDao.removeAllByUser(userService.getIdByEmail(email));

    }

    @Override
    public List<NotificationOption> getNotificationOptionsByUser(int userId) {
        return notificationDao.getNotificationOptionsByUser(userId);
    }

    @Override
    public void updateUserNotifications(List<NotificationOption> options) {
        notificationDao.updateNotificationOptions(options);
    }

    private String[] normalizeArgs(Object... args) {
        return Arrays.toString(args).replaceAll("[\\[\\]]", "").split("\\s*,\\s*");
    }
}
