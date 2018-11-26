package me.exrates.service.impl;

import com.google.common.collect.ObjectArrays;
import lombok.extern.log4j.Log4j2;
import me.exrates.exception.security.exception.PinCodeCheckNeedException;
import me.exrates.filters.CapchaAuthorizationFilter;
import me.exrates.model.User;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.PinAttempsDto;
import me.exrates.model.dto.PinDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.service.NotificationMessageService;
import me.exrates.service.NotificationsSettingsService;
import me.exrates.service.SecureService;
import me.exrates.service.UserService;
import me.exrates.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;


/**
 * Created by Maks on 28.09.2017.
 */
@Log4j2
@Service("secureServiceImpl")
public class SecureServiceImpl implements SecureService {

    private @Value("${session.checkPinParam}")
    String checkPinParam;
    private @Value("${session.authenticationParamName}")
    String authenticationParamName;
    private @Value("${session.passwordParam}")
    String passwordParam;

    @Autowired
    private NotificationMessageService notificationService;
    @Autowired
    private UserService userService;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private NotificationsSettingsService settingsService;


    @Override
    public void checkLoginAuth(HttpServletRequest request, Authentication authentication,
                               CapchaAuthorizationFilter filter) {
        request.getSession().setAttribute("2fa_".concat(NotificationMessageEventEnum.LOGIN.name()), new PinAttempsDto());
        PinDto result = reSendLoginMessage(request, authentication.getName(), false);
        if (result != null) {
            request.getSession().setAttribute(checkPinParam, "");
            request.getSession().setAttribute(authenticationParamName, authentication);
            request.getSession().setAttribute(passwordParam, request.getParameter(filter.getPasswordParameter()));
            authentication.setAuthenticated(false);
            throw new PinCodeCheckNeedException(result.getMessage());
        }
    }

    @Override
    public PinDto reSendLoginMessage(HttpServletRequest request, String userEmail, boolean forceSend) {
        int userId = userService.getIdByEmail(userEmail);
        NotificationMessageEventEnum event = NotificationMessageEventEnum.LOGIN;
        NotificationsUserSetting setting = settingsService.getByUserAndEvent(userId, event);
        if (userService.isGlobal2FaActive() || (setting != null && setting.getNotificatorId() != null)) {
            if (setting == null) {
                setting = NotificationsUserSetting.builder()
                        .notificatorId(NotificationTypeEnum.EMAIL.getCode())
                        .userId(userId)
                        .notificationMessageEventEnum(event)
                        .build();
            }
            if (setting.getNotificatorId() == null) {
                setting.setNotificatorId(NotificationTypeEnum.EMAIL.getCode());
            }
            log.debug("noty_setting {}", setting.toString());
            PinAttempsDto attempsDto = (PinAttempsDto) request.getSession().getAttribute("2fa_".concat(event.name()));
            Locale locale = localeResolver.resolveLocale(request);
            boolean needToSendPin = forceSend ? true : attempsDto.needToSendPin();
            String message;
            if (needToSendPin) {
                String newPin = messageSource.getMessage("notification.message.newPinCode", null, locale);
                message = newPin.concat(sendPinMessage(userEmail, setting, request, new String[]{IpUtils.getClientIpAddress(request, 18)}));
            } else {
                NotificationResultDto lastNotificationResultDto = (NotificationResultDto) request.getSession().getAttribute("2fa_message".concat(event.name()));
                message = messageSource.getMessage(lastNotificationResultDto.getMessageSource(), lastNotificationResultDto.getArguments(), locale);
            }
            return new PinDto(message, needToSendPin);
        }
        return null;
    }

    private NotificationsUserSetting determineSettings(NotificationsUserSetting setting, boolean canBeDisabled, int userId, NotificationMessageEventEnum event) {
        if ((setting == null || setting.getNotificatorId() == null) && !canBeDisabled) {
            return NotificationsUserSetting.builder()
                    .notificatorId(NotificationTypeEnum.EMAIL.getCode())
                    .userId(userId)
                    .notificationMessageEventEnum(event)
                    .build();
        }
        if (setting != null && setting.getNotificatorId() != null) {
            return setting;
        } else {
            return null;
        }
    }


    private String sendPinMessage(String email, NotificationsUserSetting setting, HttpServletRequest request, String[] args) {
        Locale locale = localeResolver.resolveLocale(request);
        String subject = messageSource.getMessage(setting.getNotificationMessageEventEnum().getSbjCode(), null, locale);
        String[] pin = new String[]{userService.updatePinForUserForEvent(email, setting.getNotificationMessageEventEnum())};
        String messageText = messageSource.getMessage(setting.getNotificationMessageEventEnum().getMessageCode(),
                ObjectArrays.concat(pin, args, String.class), locale);
        NotificationResultDto notificationResultDto = notificationService.notifyUser(email, messageText, subject, setting);
        request.getSession().setAttribute("2fa_message".concat(setting.getNotificationMessageEventEnum().name()), notificationResultDto);
        return messageSource.getMessage(notificationResultDto.getMessageSource(), notificationResultDto.getArguments(), locale);
    }

    @Override
    public NotificationResultDto sendLoginPincode(User user, HttpServletRequest request) {
        NotificationsUserSetting setting = getLoginSettings(user);
        Locale locale = localeResolver.resolveLocale(request);
        String subject = messageSource.getMessage(setting.getNotificationMessageEventEnum().getSbjCode(), null, locale);
        String pin = userService.updatePinForUserForEvent(user.getEmail(), setting.getNotificationMessageEventEnum());
        String messageText = messageSource.getMessage(setting.getNotificationMessageEventEnum().getMessageCode(),
                new String[] {pin}, locale);
        return notificationService.notifyUser(user.getEmail(), messageText, subject, setting);
    }

    private NotificationsUserSetting getLoginSettings(User user) {
        return  NotificationsUserSetting
                .builder()
                .notificationMessageEventEnum(NotificationMessageEventEnum.LOGIN)
                .notificatorId(NotificationMessageEventEnum.LOGIN.getCode())
                .userId(user.getId())
                .build();
    }

}
