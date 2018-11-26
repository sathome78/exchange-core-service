package me.exrates.service;

import me.exrates.filters.CapchaAuthorizationFilter;
import me.exrates.model.User;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.PinDto;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface SecureService {

    void checkLoginAuth(HttpServletRequest request, Authentication authentication,
                        CapchaAuthorizationFilter filter);

    PinDto reSendLoginMessage(HttpServletRequest request, String userEmail, boolean forceSend);

    NotificationResultDto sendLoginPincode(User user, HttpServletRequest request);
}
