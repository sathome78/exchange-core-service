package me.exrates.controller.ngcontroller.service.impl;

import me.exrates.controller.ngcontroller.exception.NgDashboardException;
import me.exrates.controller.ngcontroller.model.PasswordCreateDto;
import me.exrates.controller.ngcontroller.service.NgUserService;
import me.exrates.dao.UserDao;
import me.exrates.model.AuthTokenDto;
import me.exrates.model.Email;
import me.exrates.model.User;
import me.exrates.model.dto.TemporalToken;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserEmailDto;
import me.exrates.model.enums.IpTypesOfChecking;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.service.*;
import me.exrates.service.utils.RestApiUtils;
import me.exrates.util.IpUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class NgUserServiceImpl implements NgUserService {

    private static final Logger logger = LogManager.getLogger(NgUserServiceImpl.class);
    private final UserDao userDao;
    private final UserService userService;
    private final MessageSource messageSource;
    private final SendMailService sendMailService;
    private final AuthTokenService authTokenService;
    private final ReferralService referralService;
    private final IpBlockingService ipBlockingService;

    @Autowired
    public NgUserServiceImpl(UserDao userDao,
                             UserService userService,
                             MessageSource messageSource,
                             SendMailService sendMailService,
                             AuthTokenService authTokenService,
                             ReferralService referralService,
                             IpBlockingService ipBlockingService) {
        this.userDao = userDao;
        this.userService = userService;
        this.messageSource = messageSource;
        this.sendMailService = sendMailService;
        this.authTokenService = authTokenService;
        this.referralService = referralService;
        this.ipBlockingService = ipBlockingService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean registerUser(UserEmailDto userEmailDto, HttpServletRequest request) {

        if (!userService.ifEmailIsUnique(userEmailDto.getEmail())) {
            throw new NgDashboardException("email is exist or banned");
        }
        User user = new User();
        user.setEmail(userEmailDto.getEmail());
        if (!StringUtils.isEmpty(userEmailDto.getParentEmail())) user.setParentEmail(userEmailDto.getParentEmail());
        user.setIp(IpUtils.getClientIpAddress(request));

        if (!(userDao.create(user) && userDao.insertIp(user.getEmail(), user.getIp()))) {
            return false;
        }

        int idUser = userDao.getIdByEmail(userEmailDto.getEmail());
        user.setId(idUser);

        String host = getHost(request);

        sendEmailWithToken(user,
                TokenType.REGISTRATION,
                "emailsubmitregister.subject",
                "emailsubmitregister.text",
                Locale.ENGLISH, host, "final-registration/token?t=");

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthTokenDto createPassword(PasswordCreateDto passwordCreateDto, HttpServletRequest request) {
        String tempToken = passwordCreateDto.getTempToken();
        User user = userService.getUserByTemporalToken(tempToken);
        if (user == null) {
            logger.error("Error create password for user, temp_token {}", tempToken);
        }

        String password = RestApiUtils.decodePassword(passwordCreateDto.getPassword());
        user.setUserStatus(UserStatus.ACTIVE);
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setPassword(password);
        updateUserDto.setStatus(UserStatus.ACTIVE);
        updateUserDto.setRole(UserRole.USER);

        boolean update = userService.updateUserSettings(updateUserDto);
        if (update) {
            Optional<AuthTokenDto> authTokenResult = authTokenService.retrieveTokenNg(user.getEmail(), request);
            AuthTokenDto authTokenDto =
                    null;
            try {
                authTokenDto = authTokenResult.orElseThrow(() ->
                        new Exception("Failed to authenticate user with email: " + user.getEmail()));
            } catch (Exception e) {
                logger.error("Error creating token with email {}", user.getEmail());
            }

            authTokenDto.setReferralReference(referralService.generateReferral(user.getEmail()));
            ipBlockingService.successfulProcessing(IpUtils.getClientIpAddress(request), IpTypesOfChecking.LOGIN);
            userService.deleteTempTokenByValue(tempToken);
            return authTokenDto;
        } else {
            logger.error("Update fail, user id - {}, email - {}", user.getId(), user.getEmail());
            throw new NgDashboardException("Error while creating password");
        }
    }

    @Override
    public boolean recoveryPassword(UserEmailDto userEmailDto, HttpServletRequest request) {

        String emailIncome = userEmailDto.getEmail();
        User user = userDao.findByEmail(emailIncome);

        String host = getHost(request);

        sendEmailWithToken(user,
                TokenType.CHANGE_PASSWORD,
                "emailsubmitResetPassword.subject",
                "emailsubmitResetPassword.text",
                Locale.ENGLISH, host,
                "final-registration/token?t="); //must be changed

        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public void sendEmailWithToken(User user,
                                   TokenType tokenType,
                                   String emailSubject,
                                   String emailText,
                                   Locale locale,
                                   String host,
                                   String confirmationUrl) {
        TemporalToken token = new TemporalToken();
        token.setUserId(user.getId());
        token.setValue(UUID.randomUUID().toString());
        token.setTokenType(tokenType);
        token.setCheckIp(user.getIp());
        token.setAlreadyUsed(false);

        userService.createTemporalToken(token);

        Email email = new Email();

        confirmationUrl = confirmationUrl + token.getValue();

        email.setMessage(
                messageSource.getMessage(emailText, null, locale) +
                        " <a href='" +
                        host + "/" + confirmationUrl +
                        "'>" + messageSource.getMessage("admin.ref", null, locale) + "</a>"
        );

        email.setSubject(messageSource.getMessage(emailSubject, null, locale));
        email.setTo(user.getEmail());
        sendMailService.sendMail(email);
    }

    private String getHost(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String uri = request.getRequestURI();
        return url.substring(0, url.indexOf(uri));
    }

}
